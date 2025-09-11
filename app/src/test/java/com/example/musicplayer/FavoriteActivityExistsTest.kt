package com.example.musicplayer

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FavoriteActivityExistenceTest {

    @Test
    fun testFavoriteActivityExists() {
        val scenario = ActivityScenario.launch(FavoriteActivity::class.java)
        scenario.onActivity { activity ->
            assertNotNull(activity)
        }
        scenario.close()
    }


}
