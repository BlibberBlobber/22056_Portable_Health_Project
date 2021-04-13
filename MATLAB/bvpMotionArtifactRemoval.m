function [bvpAmplitude, smoothedBvpEnvelope] = bvpMotionArtifactRemoval(threshBVPEnv,bvpAmplitude,sampleMean)
%BVPMOTIONARTIFACTREMOVAL Summary of this function goes here
%   Detailed explanation goes here

smoothedBvpEnvelope = movmean(envelope(bvpAmplitude),sampleMean);

bvpBoolBelowThresh = smoothedBvpEnvelope < threshBVPEnv;

bvpAmplitude = bvpAmplitude.*bvpBoolBelowThresh;
end

