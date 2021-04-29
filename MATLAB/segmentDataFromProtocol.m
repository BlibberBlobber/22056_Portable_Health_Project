function [fileDataCell, classes] = segmentDataFromProtocol(fileDataCell, START, END, ORDER)
warning('off','all');

% stressNoStress = zeros(max(size(data.time)),1);

classes = ["Base" "TSST" "Fun" "Medi 1" "Medi 2" "sRead" "bRead" "fRead"];
classList = nan(size(fileDataCell{1,1}.time));
for stateIdx = 1:length(END)
    
    classList(isbetween(fileDataCell{1,1}.time,...
        fileDataCell{1,1}.time(1)+minutes(START(stateIdx)),...
        fileDataCell{1,1}.time(1)+minutes(END(stateIdx)))) = find(classes == ORDER(stateIdx));
end

fileDataCell{1,1} = [fileDataCell{1,1}, table(classList, 'VariableNames', {'Process'})];



% for i = 1:size(fileDataCell,1)
%     %if ~isequal(fileDataCell{1,2},"IBI.csv")
%     if ~isnan(fs(i))
%         
%         data = fileDataCell{i};
%         classList = nan(max(size(data.time)),1);
%         stressNoStress = zeros(max(size(data.time)),1);
%         
%         for l = 1:length(ORDER)
%             samplesOfCurrentProcess = floor((END(l) - START(l))*60*fs(i));
%             start = START(l)*60*fs(i);
%             classList(start:start+samplesOfCurrentProcess) = l;
%             
%             if ORDER(l)=="TSST"
%                 stressNoStress(start:start+samplesOfCurrentProcess) = 1;
%             end
%         end
%     else
%         continue;
%     end
%     
%     data = addvars(data,classList,'NewVariableNames','Process');
%     data = addvars(data,stressNoStress,'NewVariableNames','Stress');
%     
%     fileDataCell{i} = data;
%     
% end


warning('on','all');
end

% states = {neutral, stress, amusement}

% Baseline: 20 min with sitting standing reading magazine
% Amusement: watch eleven funny videos with 5 netrual sections in between
%                Total length = 392 seconds
% Stress: Well studied Trier Social Stress Test (public speaking and mental arithmetic)
%           5 minute speech + 5 minute arithmetic
%            Total length 10 minutes
% Meditation: 7 minutes
% Recovery: 10 min