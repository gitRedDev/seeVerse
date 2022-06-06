package com.example.seeverse

<<<<<<< HEAD
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private lateinit var navigationView: NavigationView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // Call findViewById on the DrawerLayout
        drawerLayout = findViewById(R.id.drawer_layout)

        // Pass the ActionBarToggle action into the drawerListener
        actionBarDrawerToggle = ActionBarDrawerToggle(this, drawerLayout, 0, 0)
        drawerLayout.addDrawerListener(actionBarDrawerToggle)

        // Display the hamburger icon to launch the drawer
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Call syncState() on the action bar so it'll automatically change to the back button when the drawer layout is open
        actionBarDrawerToggle.syncState()

        // Call findViewById on the NavigationView
        navigationView = findViewById(R.id.nav_view)

        // Call setNavigationItemSelectedListener on the NavigationView to detect when items are clicked
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_profile -> {
                    Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_dict -> {
                    Toast.makeText(this, "Dict", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_sign -> {
                    Toast.makeText(this, "Sign to voice", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_voice -> {
                    Toast.makeText(this, "Voice to sign", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_share -> {
                    Toast.makeText(this, "Share", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_rateUs -> {
                    Toast.makeText(this, "Rate Us", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_logout -> {
                    Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show()
                    true
                }

                else -> {
                    false
                }
            }
        }
    }

    // override the onSupportNavigateUp() function to launch the Drawer when the hamburger icon is clicked
    override fun onSupportNavigateUp(): Boolean {
        drawerLayout.openDrawer(navigationView)
        return true
    }

    // override the onBackPressed() function to close the Drawer when the back button is clicked
    override fun onBackPressed() {
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

=======
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
>>>>>>> 7159e4fc9354d367205e306fbcc1345063432f1f
}