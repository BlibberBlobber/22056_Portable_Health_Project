clc; close all; clear all;
%% Import data from specific data folder
% Locate the folder containing the -csv files of interest
% dataFolderPath = "C:\Users\olive\Google Drive\sevenWeekProject\dataFromEmpaticaE4\data_malin_test";
% Note: If error occurs switch "/" (mac)  to "\" (windows) in mainDSP and readALlCsvFromFolder

root = fileparts(which(mfilename));
addpath(genpath(fullfile(root,'Data')))
clear root

dataFolderList=dir("Data");
dataFolderPath = pwd + "/Data/" + dataFolderList(4).name; % The number should be 3:end and notes the folders containing data

[fileDataCell, dataFolderPath] = readAllCsvFromFolder(dataFolderPath); % Optional input of folder path

modalityFieldNames = string(zeros(1,size(fileDataCell,1)));
for fileIdx = 1:size(fileDataCell,1)
    modalityFieldNames(fileIdx) = convertCharsToStrings(fileDataCell{fileIdx,2}(1:end-4));
end

for i = 1:size(fileDataCell,1)
    data = fileDataCell{i,1};
    if ~isempty(data) % A must check - "tags" is empty and causes errors
        
        SerialDateUTC = string(datestr(data(1,1).Variables/86400 + datenum(1970,1,1)));
        time = datetime(SerialDateUTC,"Format","dd-MMM-uuuu HH:mm:ss.SSSSSS");
        
        if isequal(modalityFieldNames(i),"ACC")
            % Handle ACC
            
            fs = data(2,1).Variables;
            sampleDuration = seconds(1/fs);
            
            data([1,2],:) = [];
            
            tt = time+sampleDuration:sampleDuration:time+sampleDuration*size(data,1);
            
            data.time = tt';
            data.Properties.VariableNames = {'x' 'y' 'z','time'};
            
        elseif isequal(modalityFieldNames(i),"IBI")
            % Handle IBI
            tt = time+seconds(data(2:end,1).Variables);
            data([1],:) = [];
            data.Var1 = tt;
            data.Properties.VariableNames = {'time','amplitude'};
        else
            % Handle other modalities
            fs = data(2,1).Variables;
            sampleDuration = seconds(1/fs);
            
            data([1,2],:) = [];
            tt = time+sampleDuration:sampleDuration:time+sampleDuration*size(data,1);
            data = table(tt,data.Variables');
            data.Properties.VariableNames = {'time','amplitude'};
        end
    end
    
    fileDataCell{i,1} = data;
    
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




















