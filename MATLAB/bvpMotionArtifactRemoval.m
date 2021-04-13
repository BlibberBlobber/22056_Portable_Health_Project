function bvpAmplitude = bvpMotionArtifactRemoval(threshBVPEnv,bvpAmplitude,sampleMean)
%BVPMOTIONARTIFACTREMOVAL Summary of this function goes here
%   Detailed explanation goes here
bvpBoolBelowThresh = movmean(envelope(bvpAmplitude),sampleMean) < threshBVPEnv;

bvpAmplitude = bvpAmplitude.*bvpBoolBelowThresh;
end

