# Azure Versionator 🚀🦾

**Azure Versionator** is a powerful, cross-platform tool built with **Kotlin Multiplatform** and **Compose Multiplatform**. It is designed to streamline development workflows by providing a high-tech "Command Center" for managing Azure DevOps builds and data.

Designed with an industrial "Cyber-Tech" aesthetic, it offers a fast and efficient way to trigger pipelines and download resources across **Android** and **Desktop (JVM)**.

![Cyber-Tech UI Design](https://img.shields.io/badge/UI-Cyber--Tech-00E5FF?style=for-the-badge)
![KMP](https://img.shields.io/badge/Kotlin-Multiplatform-7F52FF?style=for-the-badge&logo=kotlin)
![Compose](https://img.shields.io/badge/Compose-Multiplatform-4285F4?style=for-the-badge&logo=jetpackcompose)

---

## ✨ Key Features

- **🛡️ Multi-Profile Management**: Create multiple profiles with unique Azure DevOps configurations for each organization or team project you need.
- **Profile Import & Export**: Share individual profiles as JSON files, including organization, project, variable templates and filters, without exporting your Personal Access Token (PAT).
- **🎨 Dynamic UI**: Supports Light and Dark mode.
- **💻 Desktop & Mobile**: Seamless experience between your workstation and your phone.
- **🦥LAZY-FRIENDLY** features created to simplify some of the unnecessary long tasks in Azure DevOps. Feel free to suggest new ones by creating a New Issue.

---

## ✔️ Current actions
- **⚙️ Trigger Builds**: Launch pipeline runs on specific branches and repositories with ease by creating variable templates that are much faster to fill.
- **📥 Attachment Downloader**: Bulk download work item attachments in one click! Supports **any** type of workitem. 
- **🔀 Merge Branches**: Pick a repository and a source branch (e.g. `master`), see how many commits each branch is behind, select the ones you want and merge the source into them directly. Conflicts are reported and must be resolved manually. Requires a PAT with `Code` READ & WRITE and permission to push to those branches (branch policies may block it).

---

## 🛠️ Tech Stack

- **Core**: [Kotlin Multiplatform (KMP)](https://kotlinlang.org/docs/multiplatform.html)
- **UI**: [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- **DI**: [Koin](https://insert-koin.io/) (with Navigation3 support)
- **Networking**: [Ktor](https://ktor.io/)
- **Persistence**: [Multiplatform Settings](https://github.com/russhwolf/multiplatform-settings)
- **Serialization**: [Kotlinx Serialization](https://github.com/Kotlin/kotlinx.serialization)
- **File Dialogs**: [FileKit](https://github.com/vinceglb/FileKit)

---

## 📤📥 Sharing Profiles

**Export:** Select the profile you want to share, open **Edit Profile**, and use the **Export Profile** action to save a `.json` file. Export uses the saved active profile, so save any edits before exporting.

**Import:** Use **Import Profile** on the Home screen and select a previously exported `.json` file. The app creates a new profile and makes it active without replacing existing profiles. Open **Edit Profile** to enter and save your own PAT before using Azure DevOps actions.

The file includes the profile name, organization, team project, variable templates (including values, field types, required and secret flags), and filters. The profile ID and PAT are excluded; an imported profile receives a new ID.

> **Before sharing:** JSON exports are plain text. Variable values are included even when marked **Secret**; that flag does not redact them. ***Remove any sensitive variable values from the file before sharing it.***

---

## 🚀 Getting Started

### Installation

#### 📱 Android
- **Google Play Store**: [Get it on Google Play](https://play.google.com/store/apps/details?id=dev.cjrv.azureversionator) (Coming Soon)
-  [APK in GitHub Releases](https://github.com/cjrvdev/Azure-Versionator/releases/latest)

#### 🖥️ Desktop (JVM)
- Download the executable for your OS (Windows/macOS) from the [GitHub Releases](https://github.com/cjrvdev/Azure-Versionator/releases/latest) page.

---
*Note: This project is in active development. Feel free to open issues or contribute!*

*Also... yes, this ReadMe was done with AI. Laziness is real*

