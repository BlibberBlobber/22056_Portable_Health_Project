function locs = peak_freq(input)

fs_acc = 32; % Hz
% Frequency transform
input_psd = abs((fftshift(fft(input)).^2)/length(input)); %PSD (s^2/Hz) estimation
freq_axis = -fs_acc/2:fs_acc/(length(input_psd)):...
    (fs_acc/2-fs_acc/(length(input_psd)));

[pks,locs] = findpeaks(input_psd,freq_axis, 'SortStr','descend','NPeaks',1);

% Plot power spectral density (PSD)
% figure;
% plot(freq_axis,input_psd);
% xlabel('Frequency (Hz)'); ylabel('PSD (s^2/Hz)');

end

