/*
 * Copyright (c) 2017 Proton Technologies AG
 *
 * This file is part of ProtonVPN.
 *
 * ProtonVPN is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ProtonVPN is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ProtonVPN.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.protonvpn.android.models.config;

import android.os.Build;

import com.PrimeApp;
import com.protonvpn.android.api.ApiSessionProvider;
import com.protonvpn.android.models.profiles.Profile;
import com.protonvpn.android.models.vpn.Server;
import com.protonvpn.android.utils.AndroidUtils;
import com.protonvpn.android.utils.LiveEvent;
import com.protonvpn.android.utils.Storage;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public final class UserData implements Serializable {

    private boolean connectOnBoot;
    private boolean isLoggedIn;
    private boolean showIcon;
    private boolean useSplitTunneling;
    private int mtuSize;
    private List<String> splitTunnelApps;
    private List<String> splitTunnelIpAddresses;
    private Profile defaultConnection;
    private boolean bypassLocalTraffic;
    private VpnProtocol selectedProtocol;
    private boolean secureCoreEnabled;
    private TransmissionProtocol transmissionProtocol;
    private boolean apiUseDoH;
    private NetShieldProtocol netShieldProtocol;
    private boolean useSmartProtocol;
    private boolean smartReconnect;

    private transient MutableLiveData<NetShieldProtocol> netShieldProtocolLiveData = new MutableLiveData<>(netShieldProtocol);
    private transient LiveEvent updateEvent = new LiveEvent();
    private transient ApiSessionProvider apiSessionProvider = new ApiSessionProvider();

    private transient MutableLiveData<Boolean> userPremiumLiveData = new MutableLiveData<>(true);

    public UserData() {
        apiUseDoH = false;
        bypassLocalTraffic = false;
        connectOnBoot = false;
        defaultConnection = null;
        isLoggedIn = true;
        mtuSize = 1375;
        netShieldProtocol = NetShieldProtocol.DISABLED;
        secureCoreEnabled = false;
        showIcon = true;
        smartReconnect = true;
        splitTunnelApps = new ArrayList<>();
        splitTunnelIpAddresses = new ArrayList<>();
        transmissionProtocol = TransmissionProtocol.UDP;
        useSplitTunneling = false;
        selectedProtocol = VpnProtocol.Smart;
        useSmartProtocol = true;
    }

    public String getUser() {
        return "prime";
    }

    private void saveToStorage() {
        Storage.save(this);
        updateEvent.emit();
    }

    public boolean hasAccessToServer(@Nullable Server serverToAccess) {
        return serverToAccess != null && getUserTier() >= serverToAccess.getTier();
    }

    public boolean isFreeUser() {
        return getUserTier() == 0;
    }

    public boolean isPremiumUser() {
        return getUserTier() == 1;
    }

    public int getUserTier() {
        return true ? 1 : 0;
    }

    public LiveData<Boolean> getPremiumLiveData() {
        return userPremiumLiveData;
    }

    public boolean hasAccessToAnyServer(List<Server> serverList) {
        for (Server server : serverList) {
            if (hasAccessToServer(server)) {
                return true;
            }
        }
        return false;
    }

    public Profile getDefaultConnection() {
        return defaultConnection;
    }

    public void setDefaultConnection(Profile profile) {
        defaultConnection = profile;
        saveToStorage();
    }

    public boolean getConnectOnBoot() {
        return Build.VERSION.SDK_INT < 26 && connectOnBoot;
    }

    public void setConnectOnBoot(boolean connectOnBoot) {
        this.connectOnBoot = connectOnBoot;
        saveToStorage();
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
        saveToStorage();
    }

    public boolean shouldShowIcon() {
        return Build.VERSION.SDK_INT >= 26 || showIcon;
    }

    public void setShowIcon(boolean showIcon) {
        this.showIcon = showIcon;
        saveToStorage();
    }

    public LiveEvent getUpdateEvent() {
        return updateEvent;
    }

    public int getMtuSize() {
        return mtuSize;
    }

    public void setMtuSize(int mtuSize) {
        this.mtuSize = mtuSize;
        saveToStorage();
    }

    public boolean getUseSplitTunneling() {
        return useSplitTunneling;
    }

    public void setUseSplitTunneling(boolean useSplitTunneling) {
        this.useSplitTunneling = useSplitTunneling;
        saveToStorage();
    }

    public List<String> getSplitTunnelApps() {
        return splitTunnelApps;
    }

    public void addAppToSplitTunnel(String app) {
        splitTunnelApps.add(app);
        saveToStorage();
    }

    public void addIpToSplitTunnel(String ip) {
        if (!splitTunnelIpAddresses.contains(ip)) {
            this.splitTunnelIpAddresses.add(ip);
            saveToStorage();
        }
    }

    public void removeIpFromSplitTunnel(String ip) {
        splitTunnelIpAddresses.remove(ip);
        saveToStorage();
    }

    public void removeAppFromSplitTunnel(String app) {
        splitTunnelApps.remove(app);
        saveToStorage();
    }

    @NotNull
    public List<String> getSplitTunnelIpAddresses() {
        return splitTunnelIpAddresses;
    }

    @NotNull
    public VpnProtocol getSelectedProtocol() {
        if (useSmartProtocol) {
            return VpnProtocol.Smart;
        }
        return selectedProtocol;
    }

    public boolean getUseSmartProtocol() {
        return useSmartProtocol;
    }

    public void setUseSmartProtocol(boolean value) {
        useSmartProtocol = value;
        saveToStorage();
    }

    public boolean isSmartReconnectEnabled() {
        return smartReconnect;
    }

    public void setSmartReconnectEnabled(boolean value) {
        smartReconnect = value;
        saveToStorage();
    }

    @NotNull
    public VpnProtocol getManualProtocol() {
        return selectedProtocol;
    }

    public void setManualProtocol(VpnProtocol value) {
        selectedProtocol = value;
        saveToStorage();
    }

    public TransmissionProtocol getTransmissionProtocol() {
        return transmissionProtocol;
    }

    public void setTransmissionProtocol(TransmissionProtocol transmissionProtocol) {
        this.transmissionProtocol = transmissionProtocol;
        saveToStorage();
    }

    public boolean bypassLocalTraffic() {
        return AndroidUtils.INSTANCE.isTV(PrimeApp.getAppContext()) || bypassLocalTraffic;
    }

    public void setBypassLocalTraffic(boolean bypassLocalTraffic) {
        this.bypassLocalTraffic = bypassLocalTraffic;
        saveToStorage();
    }

    public boolean isSecureCoreEnabled() {
        return secureCoreEnabled;
    }

    public void setSecureCoreEnabled(boolean secureCoreEnabled) {
        if (this.secureCoreEnabled != secureCoreEnabled) {
            this.secureCoreEnabled = secureCoreEnabled;
            saveToStorage();
        }
    }

    public void setApiUseDoH(boolean value) {
        apiUseDoH = value;
        saveToStorage();
    }

    public boolean getApiUseDoH() {
        return apiUseDoH;
    }

    public void setNetShieldProtocol(NetShieldProtocol value) {
        netShieldProtocol = value;
        netShieldProtocolLiveData.postValue(value);
        saveToStorage();
    }

    public LiveData<NetShieldProtocol> getNetShieldLiveData() {
        return netShieldProtocolLiveData;
    }

    public NetShieldProtocol getNetShieldProtocol() {
        return NetShieldProtocol.DISABLED;
    }

    public ApiSessionProvider getApiSessionProvider() {
        return apiSessionProvider;
    }
}