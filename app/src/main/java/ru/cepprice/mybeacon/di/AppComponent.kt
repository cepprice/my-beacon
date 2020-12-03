package ru.cepprice.mybeacon.di

import android.bluetooth.le.BluetoothLeScanner
import dagger.Component
import org.altbeacon.beacon.BeaconManager
import ru.cepprice.mybeacon.App
import ru.cepprice.mybeacon.ui.fragment.base.ScanningFragment
import ru.cepprice.mybeacon.ui.fragment.beacons.BeaconListFragment
import javax.inject.Singleton

@Singleton
@Component(modules = [
    ContextModule::class,
    BluetoothModule::class
])
interface AppComponent {

    fun provideBeaconManager(): BeaconManager
    fun provideBluetoothLeScanner(): BluetoothLeScanner

    fun inject(app: App)
    fun inject(fragment: ScanningFragment)
    fun inject(fragment: BeaconListFragment)
}