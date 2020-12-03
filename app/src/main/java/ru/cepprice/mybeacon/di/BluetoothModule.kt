package ru.cepprice.mybeacon.di

import android.bluetooth.BluetoothAdapter
import android.content.Context
import dagger.Module
import dagger.Provides
import org.altbeacon.beacon.BeaconManager
import org.altbeacon.beacon.BeaconParser
import javax.inject.Singleton

@Module
object BluetoothModule {

    const val IBEACON_LAYOUT = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"
    const val ALTBEACON_LAYOUT = BeaconParser.ALTBEACON_LAYOUT
    const val EDDYSTONE_TLM_LAYOUT = BeaconParser.EDDYSTONE_TLM_LAYOUT
    const val EDDYSTONE_UID_LAYOUT = BeaconParser.EDDYSTONE_UID_LAYOUT
    const val EDDYSTONE_URL_LAYOUT = BeaconParser.EDDYSTONE_URL_LAYOUT


    @JvmStatic
    @Provides
    @Singleton
    fun provideBluetoothAdapter() = BluetoothAdapter.getDefaultAdapter()

    @JvmStatic
    @Provides
    @Singleton
    fun provideBluetoothLeScanner(adapter: BluetoothAdapter) = adapter.bluetoothLeScanner

    @JvmStatic
    @Provides
    fun provideBeaconManager(context: Context): BeaconManager {

        val instance = BeaconManager.getInstanceForApplication(context)

        instance.beaconParsers.add(BeaconParser().setBeaconLayout(IBEACON_LAYOUT))
        instance.beaconParsers.add(BeaconParser().setBeaconLayout(ALTBEACON_LAYOUT))
        instance.beaconParsers.add(BeaconParser().setBeaconLayout(EDDYSTONE_TLM_LAYOUT))
        instance.beaconParsers.add(BeaconParser().setBeaconLayout(EDDYSTONE_UID_LAYOUT))
        instance.beaconParsers.add(BeaconParser().setBeaconLayout(EDDYSTONE_URL_LAYOUT))

        return instance
    }

}