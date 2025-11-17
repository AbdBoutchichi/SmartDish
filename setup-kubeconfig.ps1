# Script pour configurer le kubeconfig avec un token au lieu d'exec plugin
# Usage: .\setup-kubeconfig.ps1

param(
    [Parameter(Mandatory=$false)]
    [string]$KubeconfigBase64
)

Write-Host "=======================================" -ForegroundColor Cyan
Write-Host "CONFIGURATION KUBECONFIG" -ForegroundColor Cyan
Write-Host "=======================================" -ForegroundColor Cyan
Write-Host ""

if (-not $KubeconfigBase64) {
    Write-Host "Entrez le contenu base64 du OCI_KUBECONFIG (depuis GitHub Secrets):" -ForegroundColor Yellow
    Write-Host "(Copiez depuis: GitHub > Settings > Secrets > OCI_KUBECONFIG)" -ForegroundColor Gray
    Write-Host ""
    $KubeconfigBase64 = Read-Host "Kubeconfig base64"
}

# Créer le répertoire .kube s'il n'existe pas
$kubeDir = Join-Path $env:USERPROFILE ".kube"
if (-not (Test-Path $kubeDir)) {
    New-Item -ItemType Directory -Path $kubeDir -Force | Out-Null
    Write-Host "✅ Répertoire .kube créé" -ForegroundColor Green
}

# Décoder et sauvegarder le kubeconfig
try {
    $kubeconfigPath = Join-Path $kubeDir "config"

    # Backup de l'ancien kubeconfig
    if (Test-Path $kubeconfigPath) {
        $backupPath = Join-Path $kubeDir "config.backup.$(Get-Date -Format 'yyyyMMdd-HHmmss')"
        Copy-Item $kubeconfigPath $backupPath
        Write-Host "✅ Ancien kubeconfig sauvegardé dans: $backupPath" -ForegroundColor Green
    }

    # Décoder le base64
    $kubeconfigBytes = [System.Convert]::FromBase64String($KubeconfigBase64)
    $kubeconfigContent = [System.Text.Encoding]::UTF8.GetString($kubeconfigBytes)

    # Sauvegarder
    $kubeconfigContent | Out-File -FilePath $kubeconfigPath -Encoding UTF8 -NoNewline

    Write-Host "✅ Kubeconfig configuré dans: $kubeconfigPath" -ForegroundColor Green
    Write-Host ""

    # Vérifier si c'est un kubeconfig avec exec plugin
    if ($kubeconfigContent -match "exec:" -and $kubeconfigContent -match "oci") {
        Write-Host "⚠️  ATTENTION: Ce kubeconfig utilise l'exec plugin OCI CLI" -ForegroundColor Yellow
        Write-Host ""
        Write-Host "Vous devez installer OCI CLI:" -ForegroundColor Yellow
        Write-Host "  1. Télécharger: https://docs.oracle.com/en-us/iaas/Content/API/SDKDocs/cliinstall.htm" -ForegroundColor White
        Write-Host "  2. Ou utiliser: choco install oci-cli" -ForegroundColor White
        Write-Host ""
        Write-Host "Puis configurer OCI CLI:" -ForegroundColor Yellow
        Write-Host "  oci setup config" -ForegroundColor White
        Write-Host ""
        Write-Host "Ou demandez à votre admin de générer un kubeconfig avec token:" -ForegroundColor Yellow
        Write-Host "  oci ce cluster create-kubeconfig --cluster-id <cluster-id> --file config --region eu-paris-1 --token-version 2.0.0" -ForegroundColor White
        Write-Host ""
    } elseif ($kubeconfigContent -match "token:") {
        Write-Host "✅ Ce kubeconfig utilise un token (pas besoin d'OCI CLI)" -ForegroundColor Green
        Write-Host ""

        # Test de connexion
        Write-Host "Test de connexion au cluster..." -ForegroundColor Cyan
        $testResult = kubectl cluster-info 2>&1

        if ($LASTEXITCODE -eq 0) {
            Write-Host "✅ Connexion réussie !" -ForegroundColor Green
            Write-Host ""
            kubectl get nodes
            Write-Host ""
            Write-Host "Vous pouvez maintenant déployer avec:" -ForegroundColor Green
            Write-Host "  .\deploy-manual.ps1" -ForegroundColor White
        } else {
            Write-Host "❌ Échec de connexion" -ForegroundColor Red
            Write-Host "$testResult" -ForegroundColor Red
            Write-Host ""
            Write-Host "Le token a peut-être expiré. Demandez un nouveau kubeconfig." -ForegroundColor Yellow
        }
    } else {
        Write-Host "⚠️  Type de kubeconfig non reconnu" -ForegroundColor Yellow
        Write-Host ""
    }

} catch {
    Write-Host "❌ Erreur lors du décodage du kubeconfig: $_" -ForegroundColor Red
    Write-Host ""
    Write-Host "Vérifiez que vous avez copié le contenu base64 complet depuis GitHub Secrets" -ForegroundColor Yellow
    exit 1
}

Write-Host ""
Write-Host "=======================================" -ForegroundColor Cyan
Write-Host "Configuration terminée" -ForegroundColor Cyan
Write-Host "=======================================" -ForegroundColor Cyan

