package cz.city.honest.service.user

import cz.city.honest.dto.*
import cz.city.honest.service.gateway.external.ExternalSuggestionGateway
import cz.city.honest.service.gateway.internal.InternalUserSuggestionGateway
import cz.city.honest.service.update.PrivateUpdatable
import cz.city.honest.service.vote.VoteService
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable

class UserSuggestionService(
    private val externalSuggestionGateway: ExternalSuggestionGateway,
    private val internalUserSuggestionGateway: InternalUserSuggestionGateway,
    private val voteService: VoteService,
    private val userProvider: UserProvider
) : PrivateUpdatable {

    override fun update(accessToken: String): Observable<Unit> =
        userProvider.provide()
            .concatMap { removeSuggestions(it, accessToken);suggestSuggestions(it, accessToken) }
    //.onErrorComplete()

    fun getUserSuggestions(id: String): Flowable<UserSuggestion> =
        internalUserSuggestionGateway.getUserSuggestions(id)

    fun delete(userSuggestion: UserSuggestion) =
        voteService.delete(userSuggestion.suggestion, userSuggestion.user)
            .flatMap { internalUserSuggestionGateway.update(toDeleteStateSuggestion(userSuggestion)) }

    fun suggest(userSuggestion: UserSuggestion) =
        internalUserSuggestionGateway.suggest(userSuggestion)

    fun suggest(suggestion: Suggestion, markAs: UserSuggestionStateMarking) =
        toUserSuggestion(suggestion, markAs)
            .flatMap { suggest(it) }

    private fun toUserSuggestion(suggestion: Suggestion, markAs: UserSuggestionStateMarking) =
        userProvider.provide()
            .map { toUserSuggestion(it, suggestion, markAs) }

    private fun toUserSuggestion(
        user: User,
        suggestion: Suggestion,
        markAs: UserSuggestionStateMarking
    ) = UserSuggestion(
        user = user,
        suggestion = suggestion,
        metadata = toUserSuggestionMetadata(markAs)
    )

    private fun toDeleteStateSuggestion(userSuggestion: UserSuggestion) =
        UserSuggestion(
            user = userSuggestion.user,
            suggestion = userSuggestion.suggestion,
            metadata = getDeleteStateUserSuggestionMetadata()
        )

    private fun getDeleteStateUserSuggestionMetadata() =
        UserSuggestionMetadata(
            processed = false,
            markAs = UserSuggestionStateMarking.DELETE
        )

    private fun toUserSuggestionMetadata(markAs: UserSuggestionStateMarking) =
        UserSuggestionMetadata(
            processed = false,
            markAs = markAs
        )

    private fun removeSuggestions(user: User, accessToken: String): Observable<Unit> =
        internalUserSuggestionGateway.getUserSuggestions(user.id)
            .filter { isDeleteSuggestion(it) }
            .map { toProcessedSuggestion(it) }
            .toList()
            .toObservable()
            .flatMap { removeSuggestions(it, accessToken) }
            .map {}

    private fun removeSuggestions(
        userSuggestions: MutableList<UserSuggestion>,
        accessToken: String
    ) =
        Observable.just(getSuggestions(userSuggestions))
            .flatMap { externalSuggestionGateway.remove(accessToken, it) }
            .flatMap { deleteUserSuggestions(userSuggestions) }

    private fun deleteUserSuggestions(userSuggestions: MutableList<UserSuggestion>) =
        Observable.fromIterable(userSuggestions)
            .flatMap { internalUserSuggestionGateway.remove(it) }

    private fun suggestSuggestions(user: User, accessToken: String): Observable<Unit> =
        internalUserSuggestionGateway.getUserSuggestions(user.id)
            .filter { isNewSuggestion(it) }
            .map { toProcessedSuggestion(it) }
            .toList()
            .toObservable()
            .flatMap { suggestSuggestions(it, accessToken) }
            .map {}

    private fun toProcessedSuggestion(userSuggestion: UserSuggestion) =
        userSuggestion.copy(metadata = userSuggestion.metadata.copy(processed = true))

    private fun isDeleteSuggestion(userSuggestion: UserSuggestion) =
        isSuggestion(userSuggestion, UserSuggestionStateMarking.DELETE)

    private fun isNewSuggestion(userSuggestion: UserSuggestion) =
        isSuggestion(userSuggestion, UserSuggestionStateMarking.NEW)

    private fun isSuggestion(userSuggestion: UserSuggestion, markedAs: UserSuggestionStateMarking) =
        userSuggestion.metadata.markAs == markedAs && !userSuggestion.metadata.processed

    private fun suggestSuggestions(
        userSuggestions: MutableList<UserSuggestion>,
        accessToken: String
    ) = Observable.just(getSuggestions(userSuggestions))
        .flatMap { externalSuggestionGateway.suggest(accessToken, it) }
        .flatMap { updateUserSuggestions(userSuggestions) }

    private fun updateUserSuggestions(userSuggestions: MutableList<UserSuggestion>) =
        Observable.fromIterable(userSuggestions)
            .flatMap { internalUserSuggestionGateway.update(it) }

    private fun getSuggestions(userSuggestions: MutableList<UserSuggestion>): List<Suggestion> =
        userSuggestions.map { it.suggestion }

}