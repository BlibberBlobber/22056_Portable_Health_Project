clc;close all;clear all;
%% Import data from specific data folder
% Locate the folder containing the -csv files of interest
% dataFolderPath = "C:\Users\olive\Google Drive\sevenWeekProject\dataFromEmpaticaE4\data_malin_test";

dataFolderPath = 'C:\Users\olive\Google Drive\sevenWeekProject\dataFromEmpaticaE4\data_malin_test';
    
[fileDataCell, dataFolderPath] = readAllCsvFromFolder(dataFolderPath); % Optional input of folder path

modalityFieldNames=[];
for fileIdx = 1:size(fileDataCell,1)
    disp(convertCharsToStrings(fileDataCell{fileIdx,2}(1:end-4)))
   modalityFieldNames = append(modalityFieldNames,convertCharsToStrings(fileDataCell{fileIdx,2}(1:end-4))); 
end
modalityVarNames = {1:7};

dataColumnNames = struct(modalityFieldNames,modalityVarNames);

