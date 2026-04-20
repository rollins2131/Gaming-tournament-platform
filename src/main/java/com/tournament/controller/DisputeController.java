package com.tournament.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tournament.model.Dispute;
import com.tournament.model.Player;
import com.tournament.repository.PlayerRepository;
import com.tournament.service.DisputeService;

@RestController
@RequestMapping("/disputes")
public class DisputeController {

    private final DisputeService disputeService;
    private final PlayerRepository playerRepository;

    public DisputeController(DisputeService disputeService,
            PlayerRepository playerRepository) {
        this.disputeService = disputeService;
        this.playerRepository = playerRepository;
    }

    @PostMapping("/raise")
    public Dispute raiseDispute(@RequestParam Integer matchId,
            @RequestParam Integer playerId,
            @RequestParam String reason,
            @RequestParam(required = false) String evidenceUrl) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Player not found"));
        return disputeService.raiseDispute(matchId, player, reason, evidenceUrl);
    }

    @PostMapping("/{disputeId}/review")
    public Dispute reviewDispute(@PathVariable Integer disputeId) {
        return disputeService.markUnderReview(disputeId);
    }

    @PostMapping("/{disputeId}/accept")
    public Dispute acceptDispute(@PathVariable Integer disputeId,
            @RequestParam int scoreA,
            @RequestParam int scoreB) {
        return disputeService.acceptDispute(disputeId, scoreA, scoreB);
    }

    @PostMapping("/{disputeId}/reject")
    public Dispute rejectDispute(@PathVariable Integer disputeId) {
        return disputeService.rejectDispute(disputeId);
    }
}
