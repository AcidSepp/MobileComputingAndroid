package de.generosoft.yannick.noticeboard.util;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;

import de.generosoft.yannick.noticeboard.LoginActivity;
import de.generosoft.yannick.noticeboard.R;
import de.generosoft.yannick.noticeboard.ShowMessagesActivity;
import de.generosoft.yannick.noticeboard.ShowUserClassroomsActivity;

public class NavigationBarListener implements NavigationView.OnNavigationItemSelectedListener {

    private final String email;
    private final String password;
    private final Context context;
    private final DrawerLayout drawerLayout;

    public NavigationBarListener(final String email, final String password, final Context context, final DrawerLayout drawerLayout) {
        this.email = email;
        this.password = password;
        this.context = context;
        this.drawerLayout = drawerLayout;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        // set item as selected to persist highlight
        menuItem.setChecked(true);
        // close drawer when item is tapped
        drawerLayout.closeDrawers();
        int id = menuItem.getItemId();
        Intent intent;
        switch (id) {
            case R.id.navigation_drawer_my_messages:
                intent = new Intent(context, ShowMessagesActivity.class);
                intent.putExtra("email", email);
                intent.putExtra("password", password);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
                break;
            case R.id.navigation_drawer_my_classrooms:
                intent = new Intent(context, ShowUserClassroomsActivity.class);
                intent.putExtra("email", email);
                intent.putExtra("password", password);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
                break;
            case R.id.navigation_all_classrooms:
//                            intent = new Intent(getApplicationContext(), .class);
//                            startActivity(intent);
                break;
            case R.id.navigation_logout:
                intent = new Intent(context.getApplicationContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
                break;
        }
        return true;
    }
}
