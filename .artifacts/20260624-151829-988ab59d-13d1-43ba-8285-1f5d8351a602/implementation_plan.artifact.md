# User Management Enhancement: Design & Functionality

Improve the User Management module by aligning it with the brand's Red/Blue identity, adding user approval workflows, and implementing robust editing capabilities using a clean MVVM architecture.

## User Review Required

> [!IMPORTANT]
> - **Approval Workflow**: Pending users will have a prominent "Approve" button in both the list view (`UserRow`) and the detail panel (`UserDetailsPanel`).
> - **Admin Status Color**: Changed from purple/pink to a professional **Deep Red/Blue** to remove the "pink" look.
> - **Loading Dialogs**: All status-changing actions will trigger a centralized `ActionStatusDialog` showing loading, success, or error states.

## Proposed Changes

### Data & API Layer
Extend the API client to support verification and detailed editing.

#### [UserApi.kt](file:///C:/Users/ITCC/Documents/Android_apps/KMP_Test/shared/src/commonMain/kotlin/com/example/AdminPanel/data/api/UserApi.kt)
- Add `verifyUser(userId: String, action: String): UserResponse` (action: "approve" or "reject").
- Ensure `editUser` correctly handles the `PATCH /admin/users/<user_id>/edit/` endpoint.

---

### ViewModel Layer
Handle business logic for verification and editing.

#### [UsersViewModel.kt](file:///C:/Users/ITCC/Documents/Android_apps/KMP_Test/shared/src/commonMain/kotlin/com/example/AdminPanel/ui/users/UsersViewModel.kt)
- Implement `approveUser(userId: String)`.
- Implement `updateUser(userId: String, updates: Map<String, String?>)`.
- Use `isActionLoading`, `actionSuccess`, and `error` states to drive the UI dialogs.

---

### UI Components (Common & Design)
Refine components with the new Red/Blue theme and animations.

#### [Color.kt](file:///C:/Users/ITCC/Documents/Android_apps/KMP_Test/shared/src/commonMain/kotlin/com/example/AdminPanel/ui/theme/Color.kt)
- Define `AdminColor` as `BrandBlueDark`.
- Remove any pinkish hue from `DarkPrimary`.

#### [Common.kt](file:///C:/Users/ITCC/Documents/Android_apps/KMP_Test/shared/src/commonMain/kotlin/com/example/AdminPanel/ui/components/Common.kt)
- **DetailSection**: Add a `BrandBlue` side-accent.
- **DetailRow**: Update colors (Slate for labels, Midnight/Dark Blue for values).

---

### UI Components (Users Feature)
Add approval buttons and integrate with ViewModel.

#### [UsersContent.kt](file:///C:/Users/ITCC/Documents/Android_apps/KMP_Test/shared/src/commonMain/kotlin/com/example/AdminPanel/ui/users/UsersContent.kt)
- **UserRow**: Add a "Checkmark" icon for `Pending` users that triggers `approveUser`.
- **StatusBadge**: Update "Admin" color mapping to `BrandBlue`.
- **VerificationBadge**: Update "Pending" to use `Warning` (Amber) and "Approved" to use `Success` (Green).

#### [UserDetailsPanel.kt](file:///C:/Users/ITCC/Documents/Android_apps/KMP_Test/shared/src/commonMain/kotlin/com/example/AdminPanel/ui/users/UserDetailsPanel.kt)
- Add "Approve" button to the footer if the user is `Pending`.
- Implement full editing logic (TextFields -> PATCH request).

## Verification Plan

### Manual Verification
1. **User Approval**:
   - Filter by "Pending" verification.
   - Click "Approve" on a `UserRow`.
   - Verify the loading dialog appears, followed by a success message.
   - Verify the user's status changes to "Student" and verification to "Approved".
2. **User Editing**:
   - Open a user's details.
   - Click "Edit Profile".
   - Modify fields (e.g., Full Name, Email).
   - Click "Save" and verify the changes persist via the API.
3. **Design Check**:
   - Verify no pink colors are visible in the Admin/User views.
   - Check the new "Red/Blue" brand accents in detail cards.
