function [features] = calc_features(raw, window_size, feature_funcs)
% feature_funcs is a cell array of size(num_features, 1)

features = zeros(length(raw), size(feature_funcs,2));
for i = 1:(length(raw)-window_size)
    
    window = raw(i:i+window_size);
    
    % Define features 
    for j = 1:size(feature_funcs,2)
       features(i,j) = feature_funcs{1,j}(window);
    end
    
end

end

