function output = no_pks(input)

threshold = 0.05; % (micro Siemens)
fs = 4; % sampling frequency (Hz)
distance = 0.5; % (Seconds)
no_peaks_scr = numel(findpeaks(input,fs,'MinPeakHeight',threshold, 'MinPeakDistance', distance));
output = no_peaks_scr;

warning('off') % Warnings if no peaks are detected above the threshold

% Plot
% findpeaks(input,fs,'MinPeakHeight',threshold, 'MinPeakDistance', distance);

end

