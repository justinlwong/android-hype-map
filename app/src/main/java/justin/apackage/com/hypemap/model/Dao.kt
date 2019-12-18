package justin.apackage.com.hypemap.model

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update

@Dao
interface PostDao {
    @Query("SELECT * from posts")
    fun getPosts(): LiveData<List<PostLocation>?>

    @Query("SELECT * from posts WHERE userName = :userName")
    fun getUserPosts(userName: String): List<PostLocation>

    @Insert(onConflict = REPLACE)
    fun insert(postLocationData: PostLocation)

    @Update
    fun update(postLocationData: PostLocation)

    @Query("DELETE from posts")
    fun deleteAll()

    @Query("DELETE from posts WHERE userName = :userName")
    fun delete(userName: String)

    @Query("SELECT * from posts WHERE id = :postId")
    fun getPost(postId: String): List<PostLocation>
}

@Dao
interface UserDao {
    @Query("SELECT * from users")
    fun getUsers(): LiveData<List<User>>

    @Query("SELECT * from users")
    fun getCurrentUsers(): List<User>

    @Query("SELECT * from users WHERE userName = :userName")
    fun getUser(userName: String): User

    @Insert(onConflict = REPLACE)
    fun insert(user: User)

    @Update
    fun update(user: User)

    @Query("DELETE from users WHERE userName = :userName")
    fun delete(userName: String)

    @Query("DELETE from users")
    fun deleteAll()
}