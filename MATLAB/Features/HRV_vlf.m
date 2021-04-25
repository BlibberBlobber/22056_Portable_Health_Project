function output_vlf = HRV_vlf(oneCycleHRV)

% Frequency transform of HRV
f_resample = 8; % Hz
HRV_psd=abs((fftshift(fft(oneCycleHRV)).^2)/length(oneCycleHRV)); %PSD (s^2/Hz) estimation
freq_axis=-f_resample/2:f_resample/(length(HRV_psd)):...
    (f_resample/2-f_resample/(length(HRV_psd)));

output_vlf = trapz(HRV_psd(freq_axis(freq_axis >= 0) <= 0.04)); % VLF - HRV from 0.00 to 0.04

end

