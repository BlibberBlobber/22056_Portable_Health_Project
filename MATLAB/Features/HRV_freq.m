function total_freq = HRV_freq(oneCycleHRV)

% Frequency transform of HRV

f_resample = 8; % Hz
HRV_psd=abs((fftshift(fft(oneCycleHRV)).^2)/length(oneCycleHRV)); %PSD (s^2/Hz) estimation
freq_axis=-f_resample/2:f_resample/(length(HRV_psd)):...
    (f_resample/2-f_resample/(length(HRV_psd)));

total_freq = trapz(HRV_psd(freq_axis(freq_axis >= 0) <= 0.4)); % Total HRV Positive frequency range

% Plot power spectral density (PSD)
% figure;
% plot(freq_axis,HRV_psd);
% xlabel('Frequency (Hz)'); ylabel('PSD (s^2/Hz)');
% axis([0,0.5,0,1*10^6]); % OBS: remove axis for real representation

end

