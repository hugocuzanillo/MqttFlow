package ml.robiot.mqttflow

import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.io.BufferedInputStream
import java.io.InputStream
import java.security.KeyStore
import java.security.Security
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.inject.Inject
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManagerFactory

class SslSocketFactory @Inject constructor() {
    operator fun invoke(caCrtFileInputStream: InputStream): SSLSocketFactory? {
        Security.addProvider(BouncyCastleProvider())
        val caInput = BufferedInputStream(caCrtFileInputStream)
        val cf = CertificateFactory.getInstance("x.509")
        val ca: X509Certificate = caInput.use {
            cf.generateCertificate(it) as X509Certificate
        }
        val keyStoreType = KeyStore.getDefaultType()
        val keyStore = KeyStore.getInstance(keyStoreType).apply {
            load(null, null)
            setCertificateEntry("ca", ca)
        }
        val tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm()
        val tmf = TrustManagerFactory.getInstance(tmfAlgorithm).apply {
            init(keyStore)
        }
        val context = SSLContext.getInstance("TLSv1.2").apply {
            init(null, tmf.trustManagers, null)
        }

        return context.socketFactory
    }
}