function output_hf = HRV_hf(oneCycleHRV)

% Frequency transform of HRV
f_resample = 8; % Hz
HRV_psd=abs((fftshift(fft(oneCycleHRV)).^2)/length(oneCycleHRV)); %PSD (s^2/Hz) estimation
freq_axis=-f_resample/2:f_resample/(length(HRV_psd)):...
    (f_resample/2-f_resample/(length(HRV_psd)));

output_hf = trapz(HRV_psd(freq_axis(freq_axis >= 0.15) <= 0.4)); % HF - HRV from 0.15 to 0.40 Hz (normalize)

end