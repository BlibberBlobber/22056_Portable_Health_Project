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
% Plot Accelerometer
    subplot(2,3,1)
    plot(fileDataCell{1}.time, fileDataCell{1}.x); hold on;
    plot(fileDataCell{1}.time, fileDataCell{1}.y); hold on;
    plot(fileDataCell{1}.time, fileDataCell{1}.z); hold on;
    title("Accelerometer"); xlabel("Time"); ylabel("Amplitude");
    
for j = 2:6 % modalities
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



%% Noise removal strategy 4
threshBVPEnv = 30; sampleMean = 64*4;
tempBVP = fileDataCell{2}.amplitude;
[fileDataCell{2}.amplitude, smoothedBvpEnvelope] = bvpMotionArtifactRemoval(threshBVPEnv,fileDataCell{2}.amplitude,sampleMean);

%% Plot of motion artifact removal
figure()
tiledlayout(2,1)
ax1 = nexttile;
hold on; 
plot(tempBVP);
plot(smoothedBvpEnvelope,'--');
yline(threshBVPEnv,'-.r')
hold off
grid on
title("Blood volume pulse - Unfiltered")
legend(["BVP","Smoothed Envelope","Filter threshold"])

ax2 = nexttile;
hold on; 
plot(fileDataCell{2}.amplitude);
hold off
grid on

title("Blood volume pulse - Motion artifacts removed")
legend(["BVP"])


linkaxes([ax1 ax2],'x')

% interpolAcc = interp(sqrt(fileDataCell{1}.x.^2 + fileDataCell{1}.y.^2 + fileDataCell{1}.z.^2),2);

%% Calculate HR from BVP
[peakIndex, filtOut_BVP] = bvpPeakDetection(fileDataCell{2}.amplitude, 64, [false, false]);
thrHRV = 250;
[oneCycleHRV, oneCycleHRV_time, motionErrorTimePairs, stage4HR_resampled, stage4HR_time_resampled] = calcHRFromPeaks(peakIndex,fileDataCell{2}.time(peakIndex), 64, thrHRV, [false, true]);

%% Compare HR with Empatica HR
figure()
tiledlayout(2,1)
ax1 = nexttile;
plot(fileDataCell{4}.time, fileDataCell{4}.amplitude)
ylabel("HR [BPM]")
title("Empatica HR")

ax2 = nexttile;
plot(stage4HR_time_resampled, stage4HR_resampled)
ylabel("HR [BPM]")
title("HR repaired")

linkaxes([ax1 ax2],'x')

%% Compute SCL and SCR from EDA
[eda_scl,eda_scr] = edaRepairAndFeature(fileDataCell{3}.amplitude, fileDataCell{3}.time, motionErrorTimePairs, [true]);

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







