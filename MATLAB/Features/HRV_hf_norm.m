function output_hf_norm = HRV_hf_norm(oneCycleHRV)

% Frequency transform of HRV
f_resample = 8; % Hz
HRV_psd=abs((fftshift(fft(oneCycleHRV)).^2)/length(oneCycleHRV)); %PSD (s^2/Hz) estimation
freq_axis=-f_resample/2:f_resample/(length(HRV_psd)):...
    (f_resample/2-f_resample/(length(HRV_psd)));

% Frequency Domain Parameters

HRV_freq = trapz(HRV_psd(freq_axis(freq_axis >= 0) <= 0.4)); % Total HRV Positive frequency range
HRV_vlf = trapz(HRV_psd(freq_axis(freq_axis >= 0) <= 0.04)); % VLF - HRV from 0.00 to 0.04
HRV_hf = trapz(HRV_psd(freq_axis(freq_axis >= 0.15) <= 0.4)); % HF - HRV from 0.15 to 0.40 Hz (normalize)

% Normalised parameters
output_hf_norm = HRV_hf/(HRV_freq-HRV_vlf)*100;

end