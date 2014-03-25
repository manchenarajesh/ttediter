package in.ac.iitm.classalerts;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class NameChooserDialog extends Activity {

	EditText text;
	Button okButton,cancelButton;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_chooser_dialog);
        text = (EditText) findViewById(R.id.name);
        okButton = (Button) findViewById(R.id.okButton);
        cancelButton = (Button) findViewById(R.id.cancelButton);
        
        cancelButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent data = new Intent();
				setResult(RESULT_CANCELED,data);
				finish();
			}
		});
        
        okButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent data = new Intent();
				data.putExtra("name",text.getText().toString().trim());
				setResult(RESULT_OK,data);
				finish();
			}
		});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_name_chooser_dialog, menu);
        return true;
    }
}
