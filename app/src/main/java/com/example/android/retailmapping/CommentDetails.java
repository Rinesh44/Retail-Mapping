package com.example.android.retailmapping;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class CommentDetails extends AppCompatActivity {
    TextView shopName, comment, commentBy, category;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_details);

        toolbar = (Toolbar) findViewById(R.id.select);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("Comment Details");

        shopName = (TextView) findViewById(R.id.textShopName);
        comment = (TextView) findViewById(R.id.textComment);
        commentBy = (TextView) findViewById(R.id.textCommentedBy);
        category = (TextView) findViewById(R.id.textCategory);

        Intent i = getIntent();

        String valueShop = i.getStringExtra("shop_name");
        shopName.setText(valueShop);

        String valueCategory = i.getStringExtra("category");
        category.setText(valueCategory);

        String valueComment = i.getStringExtra("comment");
        comment.setText(valueComment);

        String valueCommentBy = i.getStringExtra("username");
        commentBy.setText(valueCommentBy);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return false;
    }

}
