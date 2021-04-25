function [fileDataCell] = removeSignalStartPart(removeSecs,fileDataCell)
%REMOVESIGNALSTARTPART Summary of this function goes here
%   Detailed explanation goes here

fileDataCell{1} = fileDataCell{1}((removeSecs*32):end,:);
fileDataCell{2} = fileDataCell{2}((removeSecs*64):end,:);
fileDataCell{3} = fileDataCell{3}((removeSecs*4):end,:);
fileDataCell{6} = fileDataCell{6}((removeSecs*4):end,:);


end

