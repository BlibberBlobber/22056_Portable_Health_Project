function [featureTable_toJoin] = resampleAllFeatures(fileDataCell,features,n_features)

acc_ = floor(size(features{4},1)/(5*32));
eda_ = floor(size(features{7},1)/(4*5)); 
hr_freq = floor(size(features{10},1)/(5*8));

feature_length = min([acc_,eda_,hr_freq]); % The length we want all the features to be
features_resampled = zeros(feature_length,n_features); 
n_current_feature = 1;

for feature_cell = 1:n_features % run through cell with feature "types"
    n = size(features{feature_cell},2);
    for feature = 1:n % run through the columns of features for every feature type
        current_feature = features{feature_cell}(:,feature)';
        current_feature = movmean(current_feature,feature_length,'Endpoints','discard');
        features_resampled(:,n_current_feature) = interp1(linspace(0,1,size(current_feature,2)), current_feature, (linspace(0,1,feature_length)));
        n_current_feature = n_current_feature + 1;
    end
end

stress = (fileDataCell{1,1}.Process==1);
stress_interp = logical(interp1(linspace(0,1,size(stress,1)), double(stress'), (linspace(0,1,feature_length))));

featureTable_toJoin = array2table(features_resampled);

featureTable_toJoin.stress = stress_interp';


end

