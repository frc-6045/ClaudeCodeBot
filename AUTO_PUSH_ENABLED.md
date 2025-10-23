# Auto-Push Enabled

This repository is configured to automatically push commits to the remote repository.

## How It Works

A git `post-commit` hook has been installed at `.git/hooks/post-commit` that runs after every commit.

**What happens:**
1. You (or I) make changes and commit them
2. Git automatically runs `git push origin <branch>`
3. Changes are immediately pushed to GitHub

## Benefits

- ✅ No need to manually push after each commit
- ✅ Remote repository always stays in sync
- ✅ Easier collaboration and backup
- ✅ Changes are immediately visible on GitHub

## To Disable

If you want to disable auto-push:

```bash
chmod -x .git/hooks/post-commit
```

## To Re-Enable

```bash
chmod +x .git/hooks/post-commit
```

## Current Status

✅ **Auto-push is ENABLED**

Future commits will be automatically pushed to `origin/main`.
