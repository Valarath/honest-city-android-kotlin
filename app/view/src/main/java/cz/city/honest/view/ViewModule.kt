package cz.city.honest.view

import cz.city.honest.view.camera.rate.RateCameraActivity
import cz.city.honest.view.camera.rate.RateCameraFragment
import cz.city.honest.view.camera.result.CameraResultActivity
import cz.city.honest.view.camera.result.CameraResultFragment
import cz.city.honest.view.camera.subject.SubjectActivity
import cz.city.honest.view.camera.subject.SubjectNameCameraFragment
import cz.city.honest.view.detail.SubjectDetailActivity
import cz.city.honest.view.detail.ui.main.ShowSubjectCostFragment
import cz.city.honest.view.detail.ui.main.ShowSubjectSuggestionsFragment
import cz.city.honest.view.filter.FilterActivity
import cz.city.honest.view.filter.FilterFragment
import cz.city.honest.view.login.LoginActivity
import cz.city.honest.view.user.UserDetailActivity
import cz.city.honest.view.user.ui.main.UserDetailSuggestionsFragment
import cz.city.honest.viewmodel.ViewModelModule
import dagger.Module
import dagger.android.AndroidInjectionModule
import dagger.android.ContributesAndroidInjector
import javax.inject.Scope

@Module(includes = [AndroidInjectionModule::class])
abstract class ViewModule {

    @ContributesAndroidInjector(modules = [ViewModelModule::class])
    internal abstract fun mapsActivity(): MapActivity

    @ContributesAndroidInjector(modules = [ViewModelModule::class])
    internal abstract fun subjectDetailActivity(): SubjectDetailActivity

    @ContributesAndroidInjector(modules = [ViewModelModule::class])
    internal abstract fun rateCameraActivity(): RateCameraActivity

    @ContributesAndroidInjector(modules = [ViewModelModule::class])
    internal abstract fun subjectActivity(): SubjectActivity

    @ContributesAndroidInjector(modules = [ViewModelModule::class])
    internal abstract fun filterActivity(): FilterActivity

    @ContributesAndroidInjector(modules = [ViewModelModule::class])
    internal abstract fun userDetailActivity(): UserDetailActivity

    @ContributesAndroidInjector(modules = [ViewModelModule::class])
    internal abstract fun loginActivity(): LoginActivity

    @ContributesAndroidInjector
    internal abstract fun showSubjectSuggestions(): ShowSubjectSuggestionsFragment

    @ContributesAndroidInjector
    internal abstract fun cameraResultActivity(): CameraResultActivity

    @ContributesAndroidInjector
    internal abstract fun rateCameraFragment(): RateCameraFragment

    @ContributesAndroidInjector
    internal abstract fun subjectNameCameraFragment(): SubjectNameCameraFragment

    @ContributesAndroidInjector
    internal abstract fun filterFragment(): FilterFragment

    @ContributesAndroidInjector
    internal abstract fun cameraResultFragment(): CameraResultFragment

    @ContributesAndroidInjector(modules = [ViewModelModule::class])
    internal abstract fun showSubjectCostFragment(): ShowSubjectCostFragment

    @ContributesAndroidInjector(modules = [ViewModelModule::class])
    internal abstract fun userDetailSuggestionsFragment(): UserDetailSuggestionsFragment
}

@Scope
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class ActivityScope