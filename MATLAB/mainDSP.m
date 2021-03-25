clc;close all;clear all;
%% Import data from specific data folder
% Locate the folder containing the -csv files of interest
% dataFolderPath = "C:\Users\olive\Google Drive\sevenWeekProject\dataFromEmpaticaE4\data_malin_test";

[fileDataCell, dataFolderPath] = readAllCsvFromFolder(); % Optional input of folder path