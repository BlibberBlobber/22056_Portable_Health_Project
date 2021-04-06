clc; close all; clear all;
%% Import data from specific data folder
% Locate the folder containing the -csv files of interest
% dataFolderPath = "C:\Users\olive\Google Drive\sevenWeekProject\dataFromEmpaticaE4\data_malin_test";
% Note: If error occurs switch "/" (mac)  to "\" (windows) in mainDSP and readALlCsvFromFolder

root = fileparts(which(mfilename));
addpath(genpath(fullfile(root,'Data')))
clear root

dataFolderList=dir("Data");
if ispc()
    dataFolderPath = pwd + "\Data\" + dataFolderList(4).name; % The number should be 3:end and notes the folders containing data
    [fileDataCell, dataFolderPath] = readAllCsvFromFolder(dataFolderPath); % Optional input of folder path
elseif ismac()
    dataFolderPath = pwd + "/Data/" + dataFolderList(4).name; % The number should be 3:end and notes the folders containing data
    [fileDataCell, dataFolderPath] = readAllCsvFromFolder(dataFolderPath); % Optional input of folder path
else
    [fileDataCell, dataFolderPath] = readAllCsvFromFolder(); % Optional input of folder path
end


%% Plot E4 data

figure; hold on; grid on;
for j = 2:6 % modalities
    
    % Plot Accelerometer
    subplot(2,3,1)
    plot(fileDataCell{1}.time, fileDataCell{1}.x); hold on;
    plot(fileDataCell{1}.time, fileDataCell{1}.y); hold on;
    plot(fileDataCell{1}.time, fileDataCell{1}.z); hold on;
    title("Accelerometer"); xlabel("Time"); ylabel("Amplitude");
    % Plot everything else
    subplot(2,3,j)
    plot(fileDataCell{j}.time, fileDataCell{j}.amplitude)
    title(modalityFieldNames(j)); xlabel("Time"); ylabel("Amplitude");
    
end
set(gcf, 'Units', 'Normalized', 'OuterPosition', [0 0 1 1]); % Create full size figure

%% Define features with sliding window

% Define length of sliding window
% Note! Remember to define window size according to sampling frequency
WINDOW_SIZE_ACC = 4; % 5 seconds
WINDOW_SIZE = 59; % 60 seconds

% Define features to calculate
% Note! Add Feature folder to path
acc_features = {@mean, @std};
bvp_features = {@mean, @std};
eda_features = {@mean, @std, @min, @max, @dynamic_range};
hr_features = {@mean, @std};
ibi_features = {@mean, @std};
temp_features = {@mean, @std, @min, @max, @dynamic_range};

% Calculate ACC features
features_acc_x = calc_features(fileDataCell{1}.x, WINDOW_SIZE_ACC, acc_features);
features_acc_y = calc_features(fileDataCell{1}.y, WINDOW_SIZE_ACC, acc_features);
features_acc_z = calc_features(fileDataCell{1}.z, WINDOW_SIZE_ACC, acc_features);

% Calculate BVP, EDA, HR, IBI and TEMP features
features_bvp = calc_features(fileDataCell{2}.amplitude, WINDOW_SIZE, bvp_features);
features_eda = calc_features(fileDataCell{3}.amplitude, WINDOW_SIZE, eda_features);
features_hr = calc_features(fileDataCell{4}.amplitude, WINDOW_SIZE, hr_features);
features_ibi = calc_features(fileDataCell{5}.amplitude, WINDOW_SIZE, ibi_features);
features_temp = calc_features(fileDataCell{6}.amplitude, WINDOW_SIZE, temp_features);




















