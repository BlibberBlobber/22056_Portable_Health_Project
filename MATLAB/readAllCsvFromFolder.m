function [fileDataCell, dataFolderPath] = readAllCsvFromFolder(dataFolderPath)
%READALLCSVFROMFOLDER Reads all .csv files in folder and returns them as a
%cell array of tables and respective filenames.
%   Optional input: dataFolderPath
if nargin < 1
    dataFolderPath = uigetdir();
end

% Locate meta data on all .csv files in folder
fileMetaStruct = dir(fullfile(dataFolderPath,'*.csv'));

% Read all .csv files to tables in combined cell array
fileDataCell = cell(size(fileMetaStruct,1),2);
for fileIdx = 1:size(fileMetaStruct,1)
    fileDataCell{fileIdx,1} = readtable(join([fileMetaStruct(fileIdx).folder, '/', fileMetaStruct(fileIdx).name],''));
    fileDataCell{fileIdx,2} = fileMetaStruct(fileIdx).name;
end
end

