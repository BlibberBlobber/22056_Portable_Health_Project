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


stress = (fileDataCell{1,1}.Process==1);
stress_interp = logical(interp1(linspace(0,1,size(stress,1)), double(stress'), (linspace(0,1,size(features{1},1)))));

featureTable_toJoin = array2table(featureTable_toJoin);

featureTable_toJoin.stress = stress_interp';

initMeasureTime = fileDataCell{1,1}.time(1);

timeVector = initMeasureTime + seconds(winLenSec:winLenSec:size(features{1},1)*winLenSec);

featureTable_toJoin.time = timeVector';

end

