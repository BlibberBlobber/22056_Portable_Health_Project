function [features] = calc_features(raw, window_size, feature_funcs)
% feature_funcs is a cell array of size(num_features, 1)
winFracOverlap = 240;

nbOfWin = floor((length(raw)*winFracOverlap/window_size) - (winFracOverlap-1));
features = zeros(nbOfWin, size(feature_funcs,2));
for idxWin = 0:nbOfWin-1 % Window loop
    
    window = raw(idxWin*(window_size/winFracOverlap)+1 : idxWin*(window_size/winFracOverlap) + window_size);
    

    for idxFeat = 1:size(feature_funcs,2) % Feature loop
       features(idxWin+1,idxFeat) = feature_funcs{1,idxFeat}(window);
    end
    
end

end

