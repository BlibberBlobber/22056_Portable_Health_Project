function output_ratio = HRV_ratio(oneCycleHRV)

% Frequency transform of HRV
f_resample = 8; % Hz
HRV_psd=abs((fftshift(fft(oneCycleHRV)).^2)/length(oneCycleHRV)); %PSD (s^2/Hz) estimation
freq_axis=-f_resample/2:f_resample/(length(HRV_psd)):...
    (f_resample/2-f_resample/(length(HRV_psd)));

HRV_lf = trapz(HRV_psd(freq_axis(freq_axis >= 0.04) <= 0.15)); % LF - HRV from 0.04 to 0.15 Hz (normalize)
HRV_hf = trapz(HRV_psd(freq_axis(freq_axis >= 0.15) <= 0.4)); % HF - HRV from 0.15 to 0.40 Hz (normalize)

output_ratio = HRV_lf/HRV_hf; % Ratio of LF to HF

end