package cz.honest.city.internal

import android.content.Context
import cz.city.honest.analyzer.rate.ExchangeRateAnalyzer
import cz.city.honest.analyzer.subject.SubjectAnalyzer
import cz.city.honest.dto.LoginData
import cz.city.honest.service.gateway.internal.InternalAuthorizationGateway
import cz.city.honest.service.gateway.internal.InternalFilterGateway
import cz.city.honest.service.gateway.internal.InternalImageNameAnalyticGateway
import cz.city.honest.service.gateway.internal.InternalImageRateAnalyticGateway
import cz.city.honest.service.provider.PropertyProvider
import cz.city.honest.service.provider.UserProvider
import cz.city.honest.service.user.UserService
import cz.honest.city.internal.analyze.ExchangeRateAnalyticGateway
import cz.honest.city.internal.analyze.SubjectNameAnalyticGateway
import cz.honest.city.internal.authentication.FacebookLoginHandler
import cz.honest.city.internal.filter.FilterSharedPreferenceRepository
import cz.honest.city.internal.provider.AndroidUserProvider
import cz.honest.city.internal.provider.ImageSubjectNameProvider
import cz.honest.city.internal.provider.rate.ImageExchangeRateProvider
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dagger.multibindings.StringKey
import javax.inject.Singleton

@Module
class InternalSourceModule {

    companion object {
        @Provides
        @Singleton
        fun getSharedPreferencesProperties(propertyProvider: PropertyProvider): SharedPreferencesProperties =
            propertyProvider.providePropertyOfType(SharedPreferencesProperties::class.java)

        @Provides
        @Singleton
        fun getFilterPersistenceHandler(context: Context, sharedPreferencesProperties: SharedPreferencesProperties): InternalFilterGateway =
            FilterSharedPreferenceRepository(context, sharedPreferencesProperties)

        @Provides
        @Singleton
        fun getExchangeRateAnalyticGateway(imageExchangeRateProvider: ImageExchangeRateProvider): InternalImageRateAnalyticGateway =
            ExchangeRateAnalyticGateway(imageExchangeRateProvider)

        @Provides
        @Singleton
        fun getExchangeNameAnalyticGateway(imageSubjectNameProvider: ImageSubjectNameProvider): InternalImageNameAnalyticGateway =
            SubjectNameAnalyticGateway(imageSubjectNameProvider)

        @Provides
        @Singleton
        fun getUserProvider(userService: UserService): UserProvider =
            AndroidUserProvider(userService)

        @Provides
        @Singleton
        fun getImageExchangeRateProvider(
            exchangeRateAnalyzers: List<@JvmSuppressWildcards ExchangeRateAnalyzer>
        ): ImageExchangeRateProvider =
            ImageExchangeRateProvider(exchangeRateAnalyzers)

        @Provides
        @Singleton
        fun getImageNameProvider(
            subjectNameAnalyzers: List<@JvmSuppressWildcards SubjectAnalyzer>
        ): ImageSubjectNameProvider =
            ImageSubjectNameProvider(subjectNameAnalyzers)

        @Provides
        @Singleton
        @IntoMap
        @StringKey("FacebookLoginData")
        fun getFacebookLoginHandler(
            userService: UserService,
            context: Context
        ): InternalAuthorizationGateway<out LoginData> =
            FacebookLoginHandler(userService, context)
    }
}

data class SharedPreferencesProperties(val repositoryName:String){
    constructor():this("")
}