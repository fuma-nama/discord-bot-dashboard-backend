# D-Dash Backend

See the [Frontend](https://github.com/SonMooSans/discord-bot-dashboard) Repository.

Required Models for your own implementation: [models.kt](src/main/kotlin/com/bdash/api/models.kt)

# Routes

GET `login`: Redirects to oauth2 authorize url

GET `callback`: Used for oauth2 redirection

* Set encrypted cookie, and navigate the user to the dashboard home page

GET `guilds`: Fetch user guilds with additional information from bot

* Login Needed
* Returns: `Array<GuildExists>`

GET `users/@me`: Fetch user information

* Login Needed
* Returns: `User` from [discord documentation](https://discord.com/developers/docs/resources/user#user-object)

HEAD `auth`: Check if user logged in

* Response: respond `200` if logged in, otherwise respond `401`

POST `auth/signout`: Sign out

* Login Needed
* Remove auth cookies

### ROUTE `guild/{guild}`

Login Needed

Admin Permissions of guild Needed
___
&nbsp; GET: Fetch guild information with specified guild id

* Returns: `Guild`

&nbsp; GET `actions`: Get actions data of the guild, won't be called if `config.data.actions` is null

* Returns: `any`, You can define your own Action Data type

&nbsp; GET `features`: Get features data of the guild

* Returns: `Features`

&nbsp; GET `detail`: Fetch Server Details for dashboard

* Returns `any`, You can define your own detail type

&nbsp; GET `detail/advanced`: Only fetched when `config.data.dashboard` has advanced row

* Returns `any`, You can define your own advanced detail type

&nbsp; GET `notification`: Get Notifications for the guild

* Returns `Array<Notification>`

&nbsp; GET `settings`: Get Settings of the guild

* Returns: `Settings`

&nbsp; PATCH `settings`: Update Settings of the guild

* Body: `Map<String, any>` updated options
* Returns: `Map<String, any>` Latest options values

### ROUTE `guild/{guild}/feature/{id}`

Login Needed

Admin Permissions of guild Needed
___
&nbsp; GET: Fetch Feature options

* Returns `Feature`

&nbsp; PATCH: Update Feature options

* Body: `Map<String, any>` updated options
* Returns: `Map<String, any>` Latest options values

&nbsp; PATCH `enabled`: Set feature enabled

* Response: `200` or `404` if feature doesn't exists

### ROUTE `guild/{guild}/action/{action}`

Login Needed

Admin Permissions of guild Needed
___
&nbsp; GET: Get specified Action Details

* Returns: `ActionDetail`

&nbsp; POST: Add new a task to Action

* Returns: `TaskDetail` Created Task details

&nbsp; GET `/{task}`: Get Task details

* Returns: `ActionDetail`

&nbsp; PATCH `/{task}`: Update Task details

* Body: `TaskBody`
* Returns `ActionDetail` Updated task details

&nbsp; DELETE `/{task}` Delete a Task

* Response: `200` or `404` if action or task doesn't exist
