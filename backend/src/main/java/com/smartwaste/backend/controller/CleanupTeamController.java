package com.smartwaste.backend.controller;

import com.smartwaste.backend.entity.CleanupTeam;
import com.smartwaste.backend.repository.CleanupTeamRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cleanup-teams")
@CrossOrigin
public class CleanupTeamController {

    private final CleanupTeamRepository cleanupTeamRepository;

    public CleanupTeamController(CleanupTeamRepository cleanupTeamRepository) {
        this.cleanupTeamRepository = cleanupTeamRepository;
    }

    // Get all cleanup teams
    @GetMapping
    public List<CleanupTeam> getAllTeams() {
        return cleanupTeamRepository.findAll();
    }

    // Create a new cleanup team
    @PostMapping
    public CleanupTeam createTeam(@RequestBody CleanupTeam team) {
        return cleanupTeamRepository.save(team);
    }

    // Get team by ID
    @GetMapping("/{id}")
    public CleanupTeam getTeamById(@PathVariable Long id) {
        return cleanupTeamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cleanup team not found"));
    }

    // Update team
    @PutMapping("/{id}")
    public CleanupTeam updateTeam(
            @PathVariable Long id,
            @RequestBody CleanupTeam updatedTeam) {

        CleanupTeam team = cleanupTeamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cleanup team not found"));

        team.setTeamName(updatedTeam.getTeamName());
        team.setTeamLeader(updatedTeam.getTeamLeader());
        team.setPhone(updatedTeam.getPhone());
        team.setStatus(updatedTeam.getStatus());

        return cleanupTeamRepository.save(team);
    }

    // Delete team
    @DeleteMapping("/{id}")
    public String deleteTeam(@PathVariable Long id) {

        cleanupTeamRepository.deleteById(id);

        return "Cleanup team deleted successfully";
    }
}