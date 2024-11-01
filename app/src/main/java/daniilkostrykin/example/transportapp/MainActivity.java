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
import android.widget.Button;
import android.webkit.WebChromeClient;  // Для обработки alert

import androidx.appcompat.app.AlertDialog;  // Для диалога

import android.Manifest;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Устанавливаем XML layout

        // Находим WebView по id
        webView = findViewById(R.id.webview_id);
        webView.getSettings().setJavaScriptEnabled(true); //  Должно быть включено
        WebView.setWebContentsDebuggingEnabled(true); // Разрешаем отладку
        webView.getSettings().setDomStorageEnabled(true);  // Включаем поддержку localStorage

        final Activity activity = this;

        // Обрабатываем WebViewClient
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

        // Устанавливаем WebChromeClient для обработки alert()
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, android.webkit.JsResult result) {
                // Отображаем сообщение alert в диалоговом окне
                showSavingsDialog(message);
                result.confirm();
                return true;
            }
        });

        // Загружаем сайт
        // webView.loadUrl("https://daniilkostrykin.github.io/Transport/");
        webView.loadUrl("file:///android_asset/index.html");

        // Находим кнопку по ID и добавляем обработчик нажатий
        Button savingsButton = findViewById(R.id.savingsButton);
        savingsButton.setOnClickListener(v -> {
            // Вызываем функцию calculateSavings() из script.js
            webView.evaluateJavascript("javascript:calculateSavings()", null);
        });
        // Запрос разрешения на запись во внешнее хранилище
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    // Метод для отображения диалогового окна
    private void showSavingsDialog(String savingsValue) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Экономия")
                .setMessage(savingsValue)  // В этом сообщении уже будет текст из alert
                .setPositiveButton("ОК", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();  // Возвращаемся на предыдущую страницу
        } else {
            super.onBackPressed();  // Если нельзя вернуться назад, то выполняется стандартное действие
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Разрешение предоставлено
            } else {
                // Разрешение отклонено, обработайте это
            }
        }
    }
}
