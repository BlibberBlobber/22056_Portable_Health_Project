function featureTable_toJoin = featureLenFixer(features, fileDataCell, winLenSec)
%FEATURELENFIXER Ensure that the lengths of each feature are identical by
%extending with mean of previous samples




for featIdx = 1:size(features,2)
    if length(features{featIdx}) > length(features{5})
        lenDiff = abs(length(features{featIdx}) - length(features{5}));
        features{featIdx} = features{featIdx}(1:end-lenDiff,:);
        
    elseif length(features{featIdx}) < length(features{5})
        lenDiff = abs(length(features{featIdx}) - length(features{5}));
        array2Append = ones(lenDiff,size(features{featIdx},2)).*features{featIdx}(end,:);
        features{featIdx} = [features{featIdx};array2Append];
        
    end
    
    if featIdx > 1
        featureTable_toJoin = [featureTable_toJoin, features{featIdx}];
    else
        featureTable_toJoin = [features{featIdx}];
    end
    
end

featureTable_toJoin = normalize(featureTable_toJoin,1);

stress = (fileDataCell{1,1}.Process == 2);
stress_interp = logical(interp1(linspace(0,1,size(stress,1)), double(stress'), (linspace(0,1,size(features{1},1)))));

interpError = stress_interp~=0 & stress_interp~=1;
stress_interp(interpError)=stress_interp([interpError(2:end),false]);

featureTable_toJoin = array2table(featureTable_toJoin);

featureTable_toJoin.stress = stress_interp';


% Only keep Baseline and stress
stressBaselineBool = fileDataCell{1,1}.Process == 1 | fileDataCell{1,1}.Process == 2;

% figure()
% subplot(5,1,1)
% plot(stressBaselineBool)

stressBaselineBool = logical(interp1(linspace(0,1,size(stressBaselineBool,1)), double(stressBaselineBool'), (linspace(0,1,size(features{1},1)))));

% subplot(5,1,2)
% plot(stressBaselineBool)

interpErrorSB = stressBaselineBool~=0 & stressBaselineBool~=1;
stressBaselineBool(interpErrorSB)=stressBaselineBool([interpErrorSB(2:end),false]);


% subplot(5,1,3)
% plot(stressBaselineBool)

% subplot(5,1,4)
% plot(featureTable_toJoin.stress)

featureTable_toJoin = featureTable_toJoin(stressBaselineBool,:);

% subplot(5,1,5)
% plot(featureTable_toJoin.stress)
% 
% saveas(gcf,['Plot' convertStringsToChars(strrep(string(now),'.','-')) '.fig']);




% initMeasureTime = fileDataCell{1,1}.time(1);

% timeVector = initMeasureTime + seconds(winLenSec:winLenSec:size(features{1},1)*winLenSec);
% 
% featureTable_toJoin.time = timeVector';

end

