package daniilkostrykin.example.transportapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import android.app.Activity;
import android.annotation.TargetApi;
import android.os.Build;
import android.widget.Button; // Импортируем класс Button
import android.view.View;     // Импортируем класс View для обработки кликов


public class MainActivity extends AppCompatActivity {

    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Устанавливаем XML layout

        // Находим WebView по id
        webView = findViewById(R.id.webview_id);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);  // Включаем поддержку localStorage


        final Activity activity = this;

        webView.setWebViewClient(new WebViewClient() {
            @SuppressWarnings("deprecation")
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(activity, description, Toast.LENGTH_SHORT).show();
            }

            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                onReceivedError(view, error.getErrorCode(), error.getDescription().toString(), request.getUrl().toString());
            }
        });

        // Загружаем сайт
        webView.loadUrl("https://daniilkostrykin.github.io/Transport/");
        Button savingsButton = findViewById(R.id.savingsButton);
        savingsButton.setOnClickListener(v -> {
            // Вызываем функцию calculateSavings() из script.js
            webView.evaluateJavascript("javascript:calculateSavings()", value -> {
                // Получаем значение экономии и выводим его, например, через Toast
                Toast.makeText(MainActivity.this, "Сэкономлено: " + value + " рублей", Toast.LENGTH_LONG).show();
            });
        });
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();  // Возвращаемся на предыдущую страницу
        } else {
            super.onBackPressed();  // Если нельзя вернуться назад, то выполняется стандартное действие
        }
    }

}
