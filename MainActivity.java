package com.example.baggegecalc;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // Input components
    private EditText etWeight, etRate;
    private RadioGroup rgClass;

    // Output components
    private TextView tvAllowance, tvExcessWeight, tvCharge, tvTax, tvTotal;

    // Application URL
    private static final String APP_URL =
            "https://github.com/mharisssyahmi-lab/BaggageCalc.git";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Load saved theme preference before creating activity
        SharedPreferences prefs = getSharedPreferences("theme_prefs", MODE_PRIVATE);
        boolean isDark = prefs.getBoolean("is_dark_mode", false);
        if (isDark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.app_name));
        }

        // Connect XML components to Java
        etWeight = findViewById(R.id.etWeight);
        etRate = findViewById(R.id.etRate);
        rgClass = findViewById(R.id.rgClass);

        tvAllowance = findViewById(R.id.tvAllowance);
        tvExcessWeight = findViewById(R.id.tvExcessWeight);
        tvCharge = findViewById(R.id.tvCharge);
        tvTax = findViewById(R.id.tvTax);
        tvTotal = findViewById(R.id.tvTotal);

        // Buttons
        Button btnCalculate = findViewById(R.id.btnCalculate);
        Button btnReset = findViewById(R.id.btnReset);

        // IME action keyboard trigger
        etRate.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                calculateFee();
                return true;
            }
            return false;
        });

        // Calculate button
        btnCalculate.setOnClickListener(v -> calculateFee());

        // Reset button
        btnReset.setOnClickListener(v -> resetCalculator());
    }

    // Main calculation method
    private void calculateFee() {
        hideKeyboard();

        String weightInput = etWeight.getText().toString().trim();
        String rateInput = etRate.getText().toString().trim();

        // Validate empty inputs
        if (weightInput.isEmpty()) {
            etWeight.setError(getString(R.string.err_empty_weight));
            etWeight.requestFocus();
            Toast.makeText(this, getString(R.string.err_incomplete_info), Toast.LENGTH_SHORT).show();
            return;
        }

        if (rateInput.isEmpty()) {
            etRate.setError(getString(R.string.err_empty_rate));
            etRate.requestFocus();
            Toast.makeText(this, getString(R.string.err_incomplete_info), Toast.LENGTH_SHORT).show();
            return;
        }

        double baggageWeight;
        double rate;

        try {
            baggageWeight = Double.parseDouble(weightInput);
            rate = Double.parseDouble(rateInput);
        } catch (NumberFormatException e) {
            Toast.makeText(this, getString(R.string.err_invalid_number), Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate negative values
        if (baggageWeight < 0) {
            etWeight.setError(getString(R.string.err_negative_weight));
            etWeight.requestFocus();
            return;
        }

        if (rate < 0) {
            etRate.setError(getString(R.string.err_negative_rate));
            etRate.requestFocus();
            return;
        }

        // Determine baggage allowance based on class
        double allowance;
        int selectedClass = rgClass.getCheckedRadioButtonId();

        if (selectedClass == R.id.rbBusiness) {
            allowance = 40.0;
        } else {
            allowance = 20.0;
        }

        // Calculate excess baggage weight
        double excessWeight = Math.max(0, baggageWeight - allowance);

        // Calculate subtotal charge
        double excessCharge = excessWeight * rate;

        // Calculate 8% government service tax
        double serviceTax = excessCharge * 0.08;

        // Calculate total payable
        double totalPayable = excessCharge + serviceTax;

        // Update UI results
        tvAllowance.setText(String.format(Locale.US, "%.2f kg", allowance));
        tvExcessWeight.setText(String.format(Locale.US, "%.2f kg", excessWeight));
        tvCharge.setText(String.format(Locale.US, "RM %.2f", excessCharge));
        tvTax.setText(String.format(Locale.US, "RM %.2f", serviceTax));
        tvTotal.setText(String.format(Locale.US, "RM %.2f", totalPayable));

        String successMsg = getString(R.string.msg_calc_success) + String.format(Locale.US, " Total Payable: RM %.2f", totalPayable);
        Toast.makeText(this, successMsg, Toast.LENGTH_LONG).show();
    }

    // Reset calculator
    private void resetCalculator() {
        hideKeyboard();

        etWeight.setText("");
        etRate.setText("");
        rgClass.check(R.id.rbEconomy);

        tvAllowance.setText(getString(R.string.default_weight));
        tvExcessWeight.setText(getString(R.string.default_weight));
        tvCharge.setText(getString(R.string.default_amount));
        tvTax.setText(getString(R.string.default_amount));
        tvTotal.setText(getString(R.string.default_amount));

        etWeight.setError(null);
        etRate.setError(null);
        etWeight.requestFocus();

        Toast.makeText(this, getString(R.string.msg_reset_success), Toast.LENGTH_SHORT).show();
    }

    // Helper to hide soft keyboard
    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    // Create toolbar menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    // Update menu title dynamically based on current theme state
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem themeItem = menu.findItem(R.id.action_theme_toggle);
        if (themeItem != null) {
            boolean isDark = (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES);
            themeItem.setTitle(isDark ? R.string.menu_theme_light : R.string.menu_theme_dark);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    // Handle toolbar menu clicks
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_share) {
            shareApplication();
            return true;
        } else if (id == R.id.action_about) {
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_theme_toggle) {
            toggleTheme();
            return true;
        } else if (id == R.id.action_quit) {
            showQuitConfirmationDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Share application URL
    private void shareApplication() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                "Check out BaggageCalc - Excess Baggage Fee Calculator!\n\n" + APP_URL);

        startActivity(Intent.createChooser(shareIntent, "Share Application"));
    }

    // Toggle theme between Light and Dark mode, save preference, and recreate activity
    private void toggleTheme() {
        SharedPreferences prefs = getSharedPreferences("theme_prefs", MODE_PRIVATE);
        boolean isDark = prefs.getBoolean("is_dark_mode", false);
        boolean newDarkState = !isDark;

        prefs.edit().putBoolean("is_dark_mode", newDarkState).apply();

        if (newDarkState) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        recreate();
    }

    // Show quit confirmation dialog
    private void showQuitConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_quit_title)
                .setMessage(R.string.dialog_quit_message)
                .setNegativeButton(R.string.dialog_btn_cancel, (dialog, which) -> dialog.dismiss())
                .setPositiveButton(R.string.dialog_btn_quit, (dialog, which) -> finishAffinity())
                .show();
    }
}
