function [fileDataCell, modalityFieldNames,fsList] = readAllCsvFromFolder(dataFolderPath)
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




modalityFieldNames = string(zeros(1,size(fileDataCell,1)));
fsList = nan(1,7);

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
            fsList(i) = fs;
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
            fsList(i) = fs;
            data(1:2,:) = [];
            tt = time+sampleDuration:sampleDuration:time+sampleDuration*size(data,1);
            
            %data = table(tt,data.Variables');
            data.time = tt';
            data.Properties.VariableNames = {'amplitude','time'};
        end
    end
    
    fileDataCell{i,1} = data;
    
end

end

