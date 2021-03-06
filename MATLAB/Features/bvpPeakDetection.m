function [peakIndex, filtOut_BVP] = bvpPeakDetection(bvp, fs, plotBool)

%% Make the bandpass filter like Pan-Tomkins do in the paper (at 5-11 instead of the wanted 5-15) using Bandpass(0.4Hz to 4 Hz) for PPG
filtObj_iir = designfilt('bandpassiir','FilterOrder',16, ...
         'HalfPowerFrequency1',0.4,'HalfPowerFrequency2',4, ...
         'SampleRate',64);

filtOut_BVP = filter(filtObj_iir,bvp);

%% Derivative
y_div = diff(filtOut_BVP);

%% Squared
y_squared = y_div.^2;

%% moving integration window
wl = 5;
yf = movsum(y_squared,wl);
yf(1:100) = 0;


%% Plot first part
xlimits = [2.9*10^4, 3*10^4];

if plotBool(1)
    figure()
    tiledlayout(3,2)
    ax1 = nexttile;
    plot(bvp)
    ylabel('BVP')


    ax2 = nexttile;
    plot(filtOut_BVP)
    ylabel('BP filtered BVP')

    ax3 = nexttile;
    plot(y_div)
    ylabel('Div')

    ax4 = nexttile;
    plot(y_squared)
    ylabel('Squared')

    ax5 = nexttile;
    plot(yf)
    ylabel('Integration')
    
    linkaxes([ax1 ax2 ax3 ax4 ax5],'x')
end


%% Fiducial Marks - Shows the plot of all found peaks and all found biggest peaks with a minimum peak distance to 40 samples (200ms)
%close all; clc;
yf = [0; yf];

[pksmpd,locsmpd] = findpeaks(yf,'MINPEAKDISTANCE', ceil(fs*0.2));
[pks,locs] = findpeaks(yf);

% figure()
% subplot(2,1,1); 
% plot(yf);  hold on;
% plot(locs,pks,'o')
% xlim(xlimits)
% ylabel("No threshold"); set(gca, 'FontSize',12);
% subplot(2,1,2); 
% plot(yf); hold on;
% plot(locsmpd,pksmpd,'o')
% xlim(xlimits)
% ylabel("250ms threshold"); set(gca, 'FontSize',12);

%%

N = length(pksmpd); % length of found peaks, that we need to control
Sthreshold = 50; %mean(yf(1:200*fs));   % init of signal threshold
Nthreshold = 2*Sthreshold;  % init of Noise threshold

Slevel = Sthreshold; % init signal level to signal threshold
Nlevel = Nthreshold; % init noise level to noise threshold
validPeaks = zeros(2,N); % matrix for valid peaks
noisePeaks = zeros(2,N); % matrix for noise peaks
countN = 0; % Counts number of found noise peaks
countS = 0; % Counts the number of Signal peaks
savedthS = zeros(2,N); savedthS(:,1) = [1 Sthreshold];
savedthN = zeros(2,N); savedthN(:,1) = [1 Nthreshold];
HR = 0; % Heart rate counter
peakIndex = zeros(1,N); % index of known QRS complex
QRS_val = zeros(1,N); % value of known QRS complex
latest_fine_RR=0;
RR_mean_8 = 0;
gb = 0; % bool for search back
T_wave_bool = 0;

for i = 1:N
% calculate heartrate for the previous 8 beats and the newest one (For
% searchback procedure). This is for comparing the mean of the 8 beats.
    if (HR >= 9)
        RR_mean_8 = mean(diff(peakIndex(HR-8:HR))); %mean of last 8 beats
        RRnew = peakIndex(HR) - peakIndex(HR-1); % latest RR interval
        
        % test for RR-AVERAGE1 (book) then halve the thresholds
        % because the
        if ((RRnew <= 0.92*RR_mean_8) || (RRnew >= 1.16*RR_mean_8))
            Sthreshold = Sthreshold/2; 
        else
            latest_fine_RR = RR_mean_8;
        end
    end
    
% if we have a regular beat or the mean of 8 QRS use this value temporary
% to look for lost QRS
    if latest_fine_RR
        temp_RR = latest_fine_RR;
    elseif RR_mean_8 && latest_fine_RR == 0
        temp_RR = RR_mean_8;
    else
        temp_RR = 0;
    end

    % if one of the previous is present, we use it. this can be viewed as a
    % typical length of an RR interval for the given situation
    if temp_RR
        % if the time between our current time and the last QRS 
        % is larger than 166% of the typical RR-interval length 
        % We surely missed some peaks and will find the bigegst peak in
        % this timespan (This must be a lost QRS)
        if (locsmpd(i) - peakIndex(HR) >= round(1.66*temp_RR))
            [pks_temp, locs_temp] = max( yf(peakIndex(HR) + 100 : locsmpd(i) - 100));
            locs_temp = peakIndex(HR) + 100 + locs_temp - 1; % Location of
        
            % if the found peak is greater than the noise threshold, we
            % save it and hope that the thresholds stabalize, as they lost
            % a real QRS
        if pks_temp > Nthreshold
            HR = HR + 1;
            QRS_val(HR) = pks_temp;
            peakIndex(HR) = locs_temp;
            
            Slevel = 0.25*pks_temp + 0.75*Slevel;
        end
        end
    end
    
    % if the peak is greater than our threshold for signal peaks, we want
    % to save it but also check for possible T_wave
    if(pksmpd(i) >= Sthreshold) 
        % when number of beats is 3 or higher as we have to make sure there
        % is enough peaks to go back and use a QRS peak for reference. This
        % is not perfect, as if there is 3 peaks that are not QRS, this
        % will fail.. 
        if HR >= 3
            % if there is no QRS peaks for 360ms we check to see if its a
            % T-wave
            if ((locsmpd(i) - peakIndex(HR)) <= round(0.36*fs))
                % check slope of integrated signal 15 samples before peak and latest QRS slope to confirm T-wave
                slope1 = mean(diff(yf(locsmpd(i) - 15 : locsmpd(i))));
                slope2 = mean(diff(yf(peakIndex(HR) - 15 : peakIndex(HR))));
                
                % if it is smaller than 1/2 of the latest QRS slope it must
                % be a T-wave (noise). And update the Noise level as it we
                % just added noise and we want to know what level of noise
                % there is in the signal.
                if abs(slope1) <= abs(slope2/2) 
                    countN = countN + 1;
                    noisePeaks(:,countN) = [locsmpd(i) pksmpd(i)];
                    T_wave_bool = 1;
                    Nlevel = 0.125*pksmpd(i) + 0.875*Nlevel;
                else
                    T_wave_bool = 0;
                end
            end
        end
        
        % if it wasnt a T-wave it must have been a real QRS, and we save it
        if T_wave_bool == 0
            HR = HR + 1; % count up numbers of QRS found
            peakIndex(HR) = locsmpd(i); % save the location in time
            QRS_val(HR) = pksmpd(i); % save the value of the peak
            % update the signal level to get closer to the "real" level of QRS
            Slevel = 0.125*pksmpd(i) + 0.875*Slevel; 
        end
    
        % if the peak is between the signal threshold and the Noise
        % threshold, we have to update the Noise level, 
    elseif (Nthreshold <= pksmpd(i) && Sthreshold > pksmpd(i)) % if we are between the signal threshold and the noise threshold
        countN = countN + 1;
        noisePeaks(:,countN) = [locsmpd(i) pksmpd(i)];
        Nlevel = 0.125*pksmpd(i) + 0.875*Nlevel;
        
    elseif (Nthreshold > pksmpd(i)) % if the peak is lower than the noise threshold mark as noise
        countN = countN + 1;
        noisePeaks(:,countN) = [locsmpd(i) pksmpd(i)];
        Nlevel = 0.125*pksmpd(i) + 0.875*Nlevel;
    end
    
% as this iteration ends we update the thresholds and save the value of
% the threshold for plotting
Sthreshold = Nlevel + 0.25*abs(Slevel - Nlevel); 
savedthS(:,i+1) = [locsmpd(i) Sthreshold];
Nthreshold = 0.5*Sthreshold; 
    
% save the levels and threshold and reset the bools
Slevels(i) = Slevel;
Nlevels(i) = Nlevel;
Sthresholds(i) = Sthreshold;
Nthresholds(i) = Nthreshold;
T_wave_bool = 0;                                                   
noise = 0; 
gb = 0;   

end



%Find out they required delay...
%QRS_index = circshift(QRS_index,-delay)

test_index = peakIndex(peakIndex~=0);

% correct for the delay the moving average made
test_index = test_index-5;
% the first peak is not gonna be a QRS peak..
test_index(1) = 25;




% correct for misplaced peaks and skewedness
for i=1:length(test_index)
   
    if test_index(i)+15 > size(bvp,1); continue; end
    temp_list = [test_index(i)-15 : test_index(i)+15];
    
    
    %[val,in] = max(filtOut_BVP(temp_list));
    [val,in] = max(bvp(temp_list));

    check = in - 16;
    
    if check == 0
        continue;
    else
        test_index(i) = test_index(i) + check;
    end   
end

test_index(1) = [];
%sum(QRS_index)
peakIndex = test_index;
%sum(QRS_index)



if plotBool(2)
    figure()
    tiledlayout(2,1)
    ax1 = nexttile;
    hold on
    plot(yf)
    plot(locsmpd(1:length(Sthresholds)),Sthresholds)
    hold off
    ylabel('Integration')
    xlabel('Time [samples]')
    legend(["Intgrated state of BVP","Signal threshold"])

    ax2 = nexttile;
    hold on
    %plot(filtOut_BVP)
    plot(bvp);
    %plot(peakIndex, filtOut_BVP(peakIndex),'o')
    plot(peakIndex, bvp(peakIndex),'o')
    hold off
    xlabel('Time [samples]')
    legend(["Filtered BVP","Detected peaks"])

    linkaxes([ax1 ax2],'x')
end
end





