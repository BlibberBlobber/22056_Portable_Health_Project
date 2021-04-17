function [fileDataCell] = removeSignalStartPart(removeSecs,fileDataCell)
%REMOVESIGNALSTARTPART Summary of this function goes here
%   Detailed explanation goes here

fileDataCell{1} = fileDataCell{1}(1:end-(removeSecs*32),:);
fileDataCell{2} = fileDataCell{2}(1:end-(removeSecs*64),:);
fileDataCell{3} = fileDataCell{3}(1:end-(removeSecs*4),:);
fileDataCell{6} = fileDataCell{6}(1:end-(removeSecs*4),:);


end

