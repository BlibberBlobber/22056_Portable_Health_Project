clc; close all; clear all;
%% Import data from specific data folder
% Locate the folder containing the -csv files of interest
% dataFolderPath = "C:\Users\olive\Google Drive\sevenWeekProject\dataFromEmpaticaE4\data_malin_test";


root = fileparts(which(mfilename));
addpath(genpath(fullfile(root,'Data')))
clear root

dataFolderList=dir("Data");
dataFolderPath = pwd + "\Data\" + dataFolderList(4).name; % The number should be 3:end and notes the folders containing data

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



%% Plot Accelerometer
figure; hold on; grid on;
plot(fileDataCell{1}.time, fileDataCell{1}.x);
plot(fileDataCell{1}.time, fileDataCell{1}.y);
plot(fileDataCell{1}.time, fileDataCell{1}.z);
title("Accelerometer"); xlabel("Date"); ylabel("Amplitude");

%%
figure; hold on; grid on;
plot(fileDataCell{2}.time, fileDataCell{2}.amplitude)
title(modalityFieldNames(2)); xlabel("xLabel"); ylabel("yLabel");

%% Plot anything else
% fileDataCell{i}.time
% fileDataCell{i}.amplitude



