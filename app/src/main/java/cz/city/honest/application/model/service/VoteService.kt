package cz.city.honest.application.model.service

import cz.city.honest.application.model.gateway.server.VoteServerSource
import cz.city.honest.application.model.repository.VoteRepository

class VoteService(val voteRepository: VoteRepository, val voteServerSource: VoteServerSource)