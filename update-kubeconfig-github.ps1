# Script pour mettre à jour le secret OCI_KUBECONFIG dans GitHub
# avec un kubeconfig qui utilise un token au lieu d'exec plugin

param(
    [Parameter(Mandatory=$false)]
    [string]$GitHubToken,

    [Parameter(Mandatory=$false)]
    [string]$KubeconfigPath = "$env:USERPROFILE\.kube\config"
)

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "MISE À JOUR OCI_KUBECONFIG DANS GITHUB" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Vérifier si le kubeconfig existe
if (-not (Test-Path $KubeconfigPath)) {
    Write-Host "❌ Erreur: Kubeconfig non trouvé à $KubeconfigPath" -ForegroundColor Red
    Write-Host ""
    Write-Host "SOLUTION:" -ForegroundColor Yellow
    Write-Host "1. Ouvrez OCI Cloud Shell : https://cloud.oracle.com/" -ForegroundColor White
    Write-Host "2. Exécutez les commandes du fichier COMMANDES-RAPIDES.txt" -ForegroundColor White
    Write-Host "3. Copiez le base64 généré" -ForegroundColor White
    Write-Host "4. Allez sur GitHub → Settings → Secrets → OCI_KUBECONFIG → Update" -ForegroundColor White
    exit 1
}

# Lire le kubeconfig
$kubeconfigContent = Get-Content $KubeconfigPath -Raw

# Vérifier si c'est un exec plugin
if ($kubeconfigContent -match 'command:\s*oci') {
    Write-Host "⚠️  ATTENTION: Votre kubeconfig utilise exec plugin OCI CLI" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Ce kubeconfig ne fonctionnera PAS dans GitHub Actions car:" -ForegroundColor Yellow
    Write-Host "  - Il nécessite OCI CLI installé" -ForegroundColor White
    Write-Host "  - Il nécessite la clé API avec passphrase" -ForegroundColor White
    Write-Host ""
    Write-Host "❌ IMPOSSIBLE D'UTILISER CE KUBECONFIG" -ForegroundColor Red
    Write-Host ""
    Write-Host "SOLUTION:" -ForegroundColor Yellow
    Write-Host "Vous DEVEZ générer un kubeconfig avec token dans OCI Cloud Shell:" -ForegroundColor White
    Write-Host ""
    Write-Host "1. Ouvrez OCI Cloud Shell : https://cloud.oracle.com/" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "2. Copiez-collez ces commandes:" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "rm -f ~/.kube/config" -ForegroundColor White
    Write-Host "oci ce cluster create-kubeconfig \" -ForegroundColor White
    Write-Host "  --cluster-id ocid1.cluster.oc1.eu-paris-1.aaaaaaaakhi5xnahycf4ozq2vinwsf3t6hbiwmomgq5quiqsvcq3gvzgw3tq \" -ForegroundColor White
    Write-Host "  --file ~/.kube/config \" -ForegroundColor White
    Write-Host "  --region eu-paris-1 \" -ForegroundColor White
    Write-Host "  --token-version 2.0.0 \" -ForegroundColor White
    Write-Host "  --kube-endpoint PUBLIC_ENDPOINT \" -ForegroundColor White
    Write-Host "  --overwrite" -ForegroundColor White
    Write-Host "" -ForegroundColor White
    Write-Host "kubectl get nodes" -ForegroundColor White
    Write-Host "cat ~/.kube/config | base64 -w 0" -ForegroundColor White
    Write-Host ""
    Write-Host "3. Copiez le résultat base64" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "4. GitHub → Settings → Secrets → OCI_KUBECONFIG → Update" -ForegroundColor Cyan
    Write-Host ""
    exit 1
}

# Vérifier si c'est un token
if ($kubeconfigContent -match 'token:\s*[A-Za-z0-9\-_\.]+') {
    Write-Host "✅ Votre kubeconfig utilise un token (CORRECT)" -ForegroundColor Green
    Write-Host ""

    # Encoder en base64
    $bytes = [System.Text.Encoding]::UTF8.GetBytes($kubeconfigContent)
    $base64 = [System.Convert]::ToBase64String($bytes)

    Write-Host "Kubeconfig encodé en base64:" -ForegroundColor Cyan
    Write-Host ""
    Write-Host $base64 -ForegroundColor White
    Write-Host ""
    Write-Host "========================================" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "PROCHAINES ÉTAPES:" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "1. Copiez le base64 ci-dessus (Ctrl+C)" -ForegroundColor White
    Write-Host "2. Allez sur: https://github.com/AbdBoutchichi/SmartDish/settings/secrets/actions" -ForegroundColor White
    Write-Host "3. Trouvez OCI_KUBECONFIG" -ForegroundColor White
    Write-Host "4. Cliquez sur Update" -ForegroundColor White
    Write-Host "5. Collez le base64" -ForegroundColor White
    Write-Host "6. Cliquez sur Update secret" -ForegroundColor White
    Write-Host "7. Faites un git push pour tester" -ForegroundColor White
    Write-Host ""
} else {
    Write-Host "⚠️  Impossible de déterminer le type d'authentification" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Contenu du kubeconfig (premières lignes):" -ForegroundColor Cyan
    ($kubeconfigContent -split "`n" | Select-Object -First 30) -join "`n"
    Write-Host ""
}

