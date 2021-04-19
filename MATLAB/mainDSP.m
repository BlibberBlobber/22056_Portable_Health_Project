clc; close all; clear all;
%% Import data from specific data folder
disp("Loading Data")
% Locate the folder containing the -csv files of interest
% dataFolderPath = "C:\Users\olive\Google Drive\sevenWeekProject\dataFromEmpaticaE4\data_malin_test";
% Note: If error occurs switch "/" (mac)  to "\" (windows) in mainDSP and readALlCsvFromFolder

root = fileparts(which(mfilename));
addpath(genpath(fullfile(root,'Data')))
addpath(genpath(fullfile(root,'Features')))
addpath(genpath(fullfile(root,'Segment_Info')))
clear root
VariableNames = {'acc_x_mean' 'acc_x_std' 'acc_x_absint' ...
    'acc_y_mean' 'acc_y_std' 'acc_y_absint'...
    'acc_z_mean' 'acc_z_std' 'acc_z_absint'...
    'acc_sum_mean' 'acc_sum_std' 'acc_sum_absint'...
    'bvp_features_mean' 'bvp_features_std'...
    'eda_features_mean' 'eda_features_std' 'eda_features_min' 'eda_features_max' 'eda_features_dynamic_range'...
    'eda_scl_features_mean' 'eda_scl_features_std'...
    'eda_scr_features_mean' 'eda_scr_features_std' 'eda_scr_features_no_pks' 'eda_scr_features_no_strong_peaks'...
    'hr_features_mean' 'hr_features_std'...
    'hrv_features_resampled_HRV_freq' 'hrv_features_resampled_HRV_vlf' 'hrv_features_resampled_HRV_lf' 'hrv_features_resampled_HRV_hf'...
    'hrv_features_resampled_HRV_ratio' 'hrv_features_resampled_HRV_hf_norm' 'hrv_features_resampled_HRV_lf_norm'...
    'temp_features_mean' 'temp_features_std' 'temp_features_min' 'temp_features_max' 'temp_features_dynamic_range'...
    'stress'};

featureTable = array2table(nan(1,40),'VariableNames',VariableNames);

for participantIndex = 3:5

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

%% Remove first part of the data
disp("Removing initial data")
removeSecs = 5;
fileDataCell = removeSignalStartPart(removeSecs,fileDataCell);

%% Get the segmentation data from quest files
disp("Importing and implemnting data segment results")
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

% figure; hold on; grid on;
% % Plot Accelerometer
%     subplot(2,3,1)
%     plot(fileDataCell{1}.time, fileDataCell{1}.x); hold on;
%     plot(fileDataCell{1}.time, fileDataCell{1}.y); hold on;
%     plot(fileDataCell{1}.time, fileDataCell{1}.z); hold on;
%     title("Accelerometer"); xlabel("Time"); ylabel("Amplitude");
%     
% for j = 2:6 % modalities
%     for l = 1:5 
%         %rectangle('Position',[datestr(fileDataCell{j}.time(1) + minutes(START(l))) -1000 datestr(fileDataCell{j}.time(1) + minutes(END(l))) 1000],'FaceColor',[0 .5 .5])
%         xline(fileDataCell{1}.time(1) + minutes(START(l)),'color','black')
%         xline(fileDataCell{1}.time(1) + minutes(END(l)),'color','red')
%     end
%     
%     % Plot everything else
%     subplot(2,3,j)
%     plot(fileDataCell{j}.time, fileDataCell{j}.amplitude)
%     title(modalityFieldNames(j)); xlabel("Time"); ylabel("Amplitude");
%     
%     
%     xline(fileDataCell{j}.time(1) + minutes(START(ORDER=="TSST")),'color','black')
%     xline(fileDataCell{j}.time(1) + minutes(END(ORDER=="TSST")),'color','red')
%     
%     %for l = 1:5 
%         %rectangle('Position',[datestr(fileDataCell{j}.time(1) + minutes(START(l))) -1000 datestr(fileDataCell{j}.time(1) + minutes(END(l))) 1000],'FaceColor',[0 .5 .5])
%         %xline(fileDataCell{j}.time(1) + minutes(START(l)),'color','black')
%         %xline(fileDataCell{j}.time(1) + minutes(END(l)),'color','red')
%     %end
%     
%     %area([fileDataCell{j}.time(1) + minutes(START(l)),fileDataCell{j}.time(1) ...
%     %    + minutes(END(l))],[100,100],'facecolor',[.8,1,.8], ...
%     %'facealpha',.5,'edgecolor','none', 'basevalue',-.2);
%     
% end
% set(gcf, 'Units', 'Normalized', 'OuterPosition', [0 0 1 1]); % Create full size figure

%% Noise removal strategy 4
disp("Removing noise from BVP data")
threshBVPEnv = 30; sampleMean = 64*4;
tempBVP = fileDataCell{2}.amplitude;
[fileDataCell{2}.amplitude, smoothedBvpEnvelope] = bvpMotionArtifactRemoval(threshBVPEnv,fileDataCell{2}.amplitude,sampleMean);

%% Plot of motion artifact removal
% figure()
% tiledlayout(2,1)
% ax1 = nexttile;
% hold on; 
% plot(tempBVP);
% plot(smoothedBvpEnvelope,'--');
% yline(threshBVPEnv,'-.r')
% hold off
% grid on
% title("Blood volume pulse - Unfiltered")
% legend(["BVP","Smoothed Envelope","Filter threshold"])
% 
% ax2 = nexttile;
% hold on; 
% plot(fileDataCell{2}.amplitude);
% hold off
% grid on
% 
% title("Blood volume pulse - Motion artifacts removed")
% legend(["BVP"])
% 
% 
% linkaxes([ax1 ax2],'x')

%% Calculate HR from BVP
disp("Calculating HR and HRV")
[peakIndex, filtOut_BVP] = bvpPeakDetection(fileDataCell{2}.amplitude, 64, [false, false]);
thrHRV = 250;

[oneCycleHRV, oneCycleHRV_time, motionErrorTimePairs, ...
    stage4HR_resampled, stage4HR_time_resampled] = ...
    calcHRFromPeaks(peakIndex,fileDataCell{2}.time(peakIndex), ...
    64, thrHRV, [false, false]);


%% Compare HR with Empatica HR
% figure()
% tiledlayout(2,1)
% ax1 = nexttile;
% plot(fileDataCell{4}.time, fileDataCell{4}.amplitude)
% ylabel("HR [BPM]")
% title("Empatica HR")
% 
% ax2 = nexttile;
% plot(stage4HR_time_resampled, stage4HR_resampled)
% ylabel("HR [BPM]")
% title("HR repaired")
% 
% linkaxes([ax1 ax2],'x')


%% Compute SCL and SCR from EDA
disp("Pre-processing of EDA")
[eda_scl,eda_scr] = edaRepairAndFeature(fileDataCell{3}.amplitude, fileDataCell{3}.time, motionErrorTimePairs, [false]);

%% Define features with sliding window

disp("Creating features")

n_features = 12;
features = cell(1,n_features);

% Define length of sliding window
WINDOW_SIZE_ACC = 5*32; % 5 seconds
WINDOW_SIZE = 60; % 60 samples
WINDOW_SIZE_EDA = 60*4; % 60 seconds
WINDOW_SIZE_HRV_resampled = 60*8; % 60 seconds

% Define features to calculate
acc_features = {@mean, @std, @abs_int}; 
acc_sum_features = {@mean, @std, @abs_int};
bvp_features = {@mean, @std};
eda_features = {@mean, @std, @min, @max, @dynamic_range};
eda_scl_features = {@mean, @std};
eda_scr_features = {@mean, @std, @no_pks, @no_strong_pks};
hr_features = {@mean, @std};
hrv_features_resampled = {@HRV_freq, @HRV_vlf, @HRV_lf, @HRV_hf, @HRV_ratio, @HRV_hf_norm, @HRV_lf_norm};

temp_features = {@mean, @std, @min, @max, @dynamic_range};

% Calculate ACC features
features_acc_x = calc_features(fileDataCell{1}.x, WINDOW_SIZE_ACC, acc_features);                       features{1} = features_acc_x;
features_acc_y = calc_features(fileDataCell{1}.y, WINDOW_SIZE_ACC, acc_features);                       features{2} = features_acc_y;
features_acc_z = calc_features(fileDataCell{1}.z, WINDOW_SIZE_ACC, acc_features);                       features{3} = features_acc_z;
% Calculate ACC features summed over all axes (3D)

acc_sum = abs(fileDataCell{1}.x) + abs(fileDataCell{1}.y) + abs(fileDataCell{1}.z);
features_acc_sum = calc_features(acc_sum, WINDOW_SIZE_ACC, acc_sum_features);							features{4} = features_acc_sum;

% Calculate BVP, EDA, HR, IBI and TEMP features
features_bvp = calc_features(fileDataCell{2}.amplitude, WINDOW_SIZE, bvp_features);                     features{5} = features_bvp;

features_eda = calc_features(fileDataCell{3}.amplitude, WINDOW_SIZE_EDA, eda_features);                 features{6} = features_eda;
features_eda_scl = calc_features(eda_scl, WINDOW_SIZE_EDA, eda_scl_features);                           features{7} = features_eda_scl;
features_eda_scr = calc_features(eda_scr, WINDOW_SIZE_EDA, eda_scr_features);                           features{8} = features_eda_scr;

features_hr = calc_features(fileDataCell{4}.amplitude, WINDOW_SIZE, hr_features);                       features{9} = features_hr;
features_hrv_frequency = calc_features(oneCycleHRV, WINDOW_SIZE_HRV_resampled, hrv_features_resampled); features{10} = features_hrv_frequency; 
features_temp = calc_features(fileDataCell{6}.amplitude, WINDOW_SIZE, temp_features);                   features{11} = features_temp;

%% Plot EDA signal

% threshold = 1; % (micro Siemens)
% fs = 4; % sampling frequency (Hz)
% distance = 0.5; % (Seconds)

% % Plot
% findpeaks(eda_scr,fs,'MinPeakHeight',threshold, 'MinPeakDistance', distance);

%% Resample all features

disp("Resampling of features")

%%  Feature selection
[idx,scores] = fscmrmr(featureTable(:,1:end-1),featureTable(:,end));

figure()
bar(scores(idx))
set(gca, 'XTick', 1:size(featureTable,2)-1)
set(gca,'XTickLabel',strrep({featureTable.Properties.VariableNames{idx}},'_','-'));
xtickangle(90)
xlabel('Predictor rank')
ylabel('Predictor importance score')

[featureTable_toJoin] = resampleAllFeatures(fileDataCell,features,n_features);

featureTable_toJoin.Properties.VariableNames = VariableNames;

featureTable = [featureTable; featureTable_toJoin];
end

