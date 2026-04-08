# GitHub Backup Plan

This plan focuses on backing up the currently committed code to a GitHub repository.

## User Review Required

> [!IMPORTANT]
> **GitHub Account**: We need to ensure you are logged into GitHub in the browser. I will help you create a repository and link it.
>
> **Authentication**: Since the GitHub CLI (`gh`) is not installed, we will use the browser to create the repository and likely use a Personal Access Token (PAT) or browser-based SSH/HTTPS for the push.

## Proposed Changes

### [Phase 1] GitHub Repository Setup

1.  **Check Login**: Use the browser to verify authentication on github.com.
2.  **Create Repository**: Create a new repository named `employee-eval`.
3.  **Get Remote URL**: Obtain the HTTPS or SSH URL for the new repository.

### [Phase 2] Linking and Pushing

1.  **Add Remote**: `git remote add origin <URL>`
2.  **Branch Configuration**: Ensure the local branch is named `main`.
3.  **Push**: `git push -u origin main`

## Open Questions

1.  **Public or Private?**: Do you want the repository to be Public or Private? (I recommend Private for an employee evaluation system).
2.  **SSH or HTTPS?**: Do you have SSH keys set up on this machine for GitHub, or should we use HTTPS with a Personal Access Token?

## Verification Plan

### Manual Verification
- Confirm the repository exists on your GitHub profile.
- Verify all project files are visible in the repository.
