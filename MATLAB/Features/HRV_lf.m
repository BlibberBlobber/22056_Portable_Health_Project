function output_lf = HRV_lf(oneCycleHRV)

% Frequency transform of HRV
f_resample = 8; % Hz
HRV_psd=abs((fftshift(fft(oneCycleHRV)).^2)/length(oneCycleHRV)); %PSD (s^2/Hz) estimation
freq_axis=-f_resample/2:f_resample/(length(HRV_psd)):...
    (f_resample/2-f_resample/(length(HRV_psd)));

output_lf = trapz(HRV_psd(freq_axis(freq_axis >= 0.04) <= 0.15)); % LF - HRV from 0.04 to 0.15 Hz (normalize)

end