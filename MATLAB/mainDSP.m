clc; close all; clear all;
%% Import data from specific data folder
% Locate the folder containing the -csv files of interest
% dataFolderPath = "C:\Users\olive\Google Drive\sevenWeekProject\dataFromEmpaticaE4\data_malin_test";
% Note: If error occurs switch "/" (mac)  to "\" (windows) in mainDSP and readALlCsvFromFolder

root = fileparts(which(mfilename));
addpath(genpath(fullfile(root,'Data')))
addpath(genpath(fullfile(root,'Features')))
addpath(genpath(fullfile(root,'Segment_Info')))
clear root

participantIndex = 3;

dataFolderList=dir("Data");
if ispc()
    dataFolderPath = pwd + "\Data\" + dataFolderList(participantIndex).name; % The number should be 3:end and notes the folders containing data
    [fileDataCell, modalityFieldNames, fs] = readAllCsvFromFolder(dataFolderPath); % Optional input of folder path
elseif ismac()
    dataFolderPath = pwd + "/Data/" + dataFolderList(participantIndex).name; % The number should be 3:end and notes the folders containing data
    [fileDataCell, modalityFieldNames, fs] = readAllCsvFromFolder(dataFolderPath); % Optional input of folder path
else
    [fileDataCell, modalityFieldNames, fs] = readAllCsvFromFolder(); % Optional input of folder path
end

%% Get the segmentation data from quest files
segmentFolderList=dir("Segment_Info");
segmentFolderPath = pwd + "\Segment_Info\" + segmentFolderList(participantIndex).name;

table = importSegmentInfo(segmentFolderPath, [2 4]);
% Get Order of protocol and start and end times
ORDER = table(1,2:6);
START = str2double(table(2,2:6));
END = str2double(table(3,2:6));

segmentDataFromProtocol(fileDataCell, START, END, ORDER, fs);

[fileDataCell] = segmentDataFromProtocol(fileDataCell, START, END, ORDER, fs);

%% Plot E4 data
figure; hold on; grid on;
for j = 2:6 % modalities
    
    % Plot Accelerometer
    subplot(2,3,1)
    plot(fileDataCell{1}.time, fileDataCell{1}.x); hold on;
    plot(fileDataCell{1}.time, fileDataCell{1}.y); hold on;
    plot(fileDataCell{1}.time, fileDataCell{1}.z); hold on;
    title("Accelerometer"); xlabel("Time"); ylabel("Amplitude");
    
    for l = 1:5 
        %rectangle('Position',[datestr(fileDataCell{j}.time(1) + minutes(START(l))) -1000 datestr(fileDataCell{j}.time(1) + minutes(END(l))) 1000],'FaceColor',[0 .5 .5])
        xline(fileDataCell{1}.time(1) + minutes(START(l)),'color','black')
        xline(fileDataCell{1}.time(1) + minutes(END(l)),'color','red')
    end
    
    % Plot everything else
    subplot(2,3,j)
    plot(fileDataCell{j}.time, fileDataCell{j}.amplitude)
    title(modalityFieldNames(j)); xlabel("Time"); ylabel("Amplitude");
    
    
    xline(fileDataCell{j}.time(1) + minutes(START(ORDER=="TSST")),'color','black')
    xline(fileDataCell{j}.time(1) + minutes(END(ORDER=="TSST")),'color','red')
    
    %for l = 1:5 
        %rectangle('Position',[datestr(fileDataCell{j}.time(1) + minutes(START(l))) -1000 datestr(fileDataCell{j}.time(1) + minutes(END(l))) 1000],'FaceColor',[0 .5 .5])
        %xline(fileDataCell{j}.time(1) + minutes(START(l)),'color','black')
        %xline(fileDataCell{j}.time(1) + minutes(END(l)),'color','red')
    %end
    
    %area([fileDataCell{j}.time(1) + minutes(START(l)),fileDataCell{j}.time(1) ...
    %    + minutes(END(l))],[100,100],'facecolor',[.8,1,.8], ...
    %'facealpha',.5,'edgecolor','none', 'basevalue',-.2);
    
end
set(gcf, 'Units', 'Normalized', 'OuterPosition', [0 0 1 1]); % Create full size figure

%% Noise removal stategy 1
% accFs = 32;
% secsToSmooth = 15;
% smoothingNbSamples = secsToSmooth*accFs;
% movMeanEnvDiffAcc = movmean(envelope(sqrt(fileDataCell{1}.x.^2 + fileDataCell{1}.y.^2 + fileDataCell{1}.z.^2)),smoothingNbSamples);
% 
% accThresh = 66.5;
% accBoolBelowThresh = movMeanEnvDiffAcc < accThresh;
% 
% 
% xlimits = [fileDataCell{1}.time(25*60*accFs+1),fileDataCell{1}.time(36*60*accFs+1)];
% 
% figure
% subplot(3,2,1)
% hold on
% plot(fileDataCell{1}.time + datenum(0,0,0,0,0,4), movMeanEnvDiffAcc)
% yline(accThresh)
% hold off
% ylabel('movMean(Env(length(acc)))')
% xlim(xlimits)
% 
% subplot(3,2,2)
% plot(fileDataCell{1}.time,fileDataCell{1}.x)
% ylabel('Acc x')
% xlim(xlimits)
% 
% subplot(3,2,3)
% hold on
% plot(fileDataCell{2}.time,fileDataCell{2}.amplitude)
% plot(fileDataCell{1}.time, accBoolBelowThresh*200)
% hold off
% ylabel('BVP')
% xlim(xlimits)
% 
% subplot(3,2,4)
% plot(fileDataCell{1}.time,fileDataCell{1}.y)
% ylabel('Acc y')
% xlim(xlimits)
% 
% subplot(3,2,5)
% plot(fileDataCell{2}.time(4:end),fileDataCell{2}.amplitude(4:end) .* interp(double(accBoolBelowThresh),2)')
% ylabel('Acc thresh filtered BVP')
% xlim(xlimits)
% 
% subplot(3,2,6)
% plot(fileDataCell{1}.time,fileDataCell{1}.z)
% ylabel('Acc z')
% xlim(xlimits)
% 
% 
% fileDataCell{2}.amplitude(4:end) = fileDataCell{2}.amplitude(4:end) .* interp(double(accBoolBelowThresh),2)';

%% Noise removal stategy 2
% filtObj_iir = designfilt('bandpassiir','FilterOrder',16, ...
%          'HalfPowerFrequency1',0.4,'HalfPowerFrequency2',4, ...
%          'SampleRate',64);
%      
% xStart = 3*10^3;
% xlimits = [xStart xStart+5*10^2];
% figure()
% subplot(3,1,1)
% hold on
% plot(fileDataCell{2}.amplitude)
% plot(envelope(fileDataCell{2}.amplitude),'--')
% ylabel("Raw BVP")
% xlim(xlimits)
% hold off
% 
% subplot(3,1,2)
% plot(filter(filtObj_iir,fileDataCell{2}.amplitude./ (envelope(fileDataCell{2}.amplitude))))
% ylabel("BVP/env(BVP)")
% xlim(xlimits)
% 
% 
% subplot(3,1,3)
% plot(filter(filtObj_iir,fileDataCell{2}.amplitude./ (envelope(fileDataCell{2}.amplitude.^2))))
% ylabel("BVP/env(BVP)^2")
% xlim(xlimits)

%% Noise removal stategy 3
% method = "LMS";
% stepSize = 0.6;
% forgettingFactor = 0.4;
% nbOfCoeff = 10;
% 
% xStart = 1*10^3+1;
% xlimits = [xStart xStart+5*10^2];
% 
% dataOffset = 13;%samples
% 
% BVPSnip = fileDataCell{2}.amplitude(xlimits(1):xlimits(2));
% interpolAcc = interp(sqrt(fileDataCell{1}.x.^2 + fileDataCell{1}.y.^2 + fileDataCell{1}.z.^2),2);
% accSnip = interpolAcc(xlimits(1)+dataOffset:xlimits(2)+dataOffset)';
% 
% Create filter obj
% lms = dsp.LMSFilter('Length',nbOfCoeff, 'Method','Normalized LMS', 'StepSize',stepSize,'LeakageFactor',forgettingFactor);%,'InitialConditions',coeffInitCondition);
% 
% Filtering
% [y,err,wtslms] = lms(accSnip'./mean(accSnip), BVPSnip');
% xPlotLim = [0,510];
% 
% figure();
% subplot(3,1,1)
% plot(BVPSnip)
% ylabel("BVP")
% xlim(xPlotLim)
% 
% subplot(3,1,2)
% plot(accSnip'./mean(accSnip))
% ylabel("ACC")
% xlim(xPlotLim)
% 
% subplot(3,1,3)
% hold on
% plot(BVPSnip)
% plot(err)
% hold off
% legend(["BVP orig","BVP Filt"])
% ylabel("BVP and err")
% xlim(xPlotLim)

%% Noise removal strategy 4
threshBVPEnv = 30;
bvpBoolBelowThresh = movmean(envelope(fileDataCell{2}.amplitude),64*4) < threshBVPEnv;

interpolAcc = interp(sqrt(fileDataCell{1}.x.^2 + fileDataCell{1}.y.^2 + fileDataCell{1}.z.^2),2);

figure()
tiledlayout(2,2)
ax1 = nexttile;
hold on; 
plot(fileDataCell{2}.amplitude);
plot(envelope(fileDataCell{2}.amplitude),'--');
hold off
grid on
ylabel("BVP")

ax2 = nexttile;
hold on; 
plot(interpolAcc);
plot(envelope(interpolAcc),'--');
hold off
grid on
ylabel("ACC")

ax4 = nexttile;
plot(movmean(envelope(fileDataCell{2}.amplitude),64*4));
grid on
ylabel("BVP smoothed envelope")

ax3 = nexttile;
plot(movmean(envelope(interpolAcc),64*4));
grid on
ylabel("ACC smoothed envelope")

linkaxes([ax1 ax2 ax3 ax4],'x')

fileDataCell{2}.amplitude = fileDataCell{2}.amplitude.*bvpBoolBelowThresh;

%% Calculate HR from BVP

[peakIndex, filtOut_BVP] = bvpPeakDetection(fileDataCell{2}.amplitude, 64);

figure()
hold on
plot(fileDataCell{2}.time, fileDataCell{2}.amplitude,'b')
plot(fileDataCell{2}.time(peakIndex), fileDataCell{2}.amplitude(peakIndex),'bo')
hold off

%% Compute SCL and SCR from EDA

eda = fileDataCell{3}.amplitude;
eda_scl = movmean(eda,[51 0]); % EDA is sampled at 4 Hz; X samples backward
eda_scr = eda - eda_scl;

figure;
subplot(2,1,1)
plot(fileDataCell{3}.time, eda,':','LineWidth',0.8)
title('SCL'); xlabel("Time"); ylabel("Amplitude (\muS)"); hold on;
plot(fileDataCell{3}.time, eda_scl)
subplot(2,1,2)
plot(fileDataCell{3}.time, eda_scr)
title('SCR'); xlabel("Time"); ylabel("Amplitude");

legend()

%% Define features with sliding window

% Define length of sliding window
WINDOW_SIZE_ACC = 5*32; % 5 seconds
WINDOW_SIZE = 60; % 60 samples
WINDOW_SIZE_EDA = 60*4; % 60 seconds

% Define features to calculate
acc_features = {@mean, @std, @abs_int};
acc_sum_features = {@mean, @std, @abs_int};
bvp_features = {@mean, @std};
eda_features = {@mean, @std, @min, @max, @dynamic_range};
eda_scl_features = {@mean, @std};
eda_scr_features = {@mean, @std, @no_pks, @no_strong_pks};
hr_features = {@mean, @std};
ibi_features = {@mean, @std};
temp_features = {@mean, @std, @min, @max, @dynamic_range};

% Calculate ACC features
features_acc_x = calc_features(fileDataCell{1}.x, WINDOW_SIZE_ACC, acc_features);
features_acc_y = calc_features(fileDataCell{1}.y, WINDOW_SIZE_ACC, acc_features);
features_acc_z = calc_features(fileDataCell{1}.z, WINDOW_SIZE_ACC, acc_features);
% Calculate ACC features summed over all axes (3D)
acc_sum = fileDataCell{1}.x + fileDataCell{1}.y + fileDataCell{1}.z;
features_acc_sum = calc_features(acc_sum, WINDOW_SIZE_ACC, acc_sum_features);

% Calculate BVP, EDA, HR, IBI and TEMP features
features_bvp = calc_features(fileDataCell{2}.amplitude, WINDOW_SIZE, bvp_features);

features_eda = calc_features(fileDataCell{3}.amplitude, WINDOW_SIZE_EDA, eda_features);
features_eda_scl = calc_features(eda_scl, WINDOW_SIZE_EDA, eda_scl_features);
features_eda_scr = calc_features(eda_scr, WINDOW_SIZE_EDA, eda_scr_features);

features_hr = calc_features(fileDataCell{4}.amplitude, WINDOW_SIZE, hr_features);
features_ibi = calc_features(fileDataCell{5}.amplitude, WINDOW_SIZE, ibi_features);
features_temp = calc_features(fileDataCell{6}.amplitude, WINDOW_SIZE, temp_features);

%% Plot EDA signal
% 
% threshold = 1; % (micro Siemens)
% fs = 4; % sampling frequency (Hz)
% distance = 0.5; % (Seconds)
% 
% % Plot
% findpeaks(eda_scr,fs,'MinPeakHeight',threshold, 'MinPeakDistance', distance);





%% List of features we need to compute:

% HRV
% Everything..







