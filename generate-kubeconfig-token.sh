#!/bin/bash
# Script pour générer un kubeconfig OKE avec token (sans dépendance OCI CLI)
# À exécuter dans OCI Cloud Shell

set -e

CLUSTER_ID="ocid1.cluster.oc1.eu-paris-1.aaaaaaaakhi5xnahycf4ozq2vinwsf3t6hbiwmomgq5quiqsvcq3gvzgw3tq"
REGION="eu-paris-1"

echo "========================================"
echo "GÉNÉRATION KUBECONFIG OKE AVEC TOKEN"
echo "========================================"
echo ""

# Supprimer l'ancien kubeconfig
rm -f ~/.kube/config

# Générer le kubeconfig avec token (pas d'exec plugin)
echo "Génération du kubeconfig avec token..."
oci ce cluster create-kubeconfig \
  --cluster-id "$CLUSTER_ID" \
  --file ~/.kube/config \
  --region "$REGION" \
  --token-version 2.0.0 \
  --kube-endpoint PUBLIC_ENDPOINT \
  --overwrite

echo "✅ Kubeconfig généré"
echo ""

# Vérifier que le kubeconfig fonctionne
echo "Test de connexion au cluster..."
kubectl get nodes

if [ $? -eq 0 ]; then
  echo ""
  echo "✅ Connexion réussie !"
  echo ""

  # Encoder en base64
  echo "========================================"
  echo "KUBECONFIG EN BASE64"
  echo "========================================"
  echo ""
  echo "Copiez le contenu ci-dessous et mettez-le dans le secret GitHub OCI_KUBECONFIG:"
  echo ""
  cat ~/.kube/config | base64 -w 0
  echo ""
  echo ""
  echo "========================================"
  echo "✅ TERMINÉ"
  echo "========================================"
  echo ""
  echo "Prochaines étapes:"
  echo "1. Copiez le contenu base64 ci-dessus"
  echo "2. GitHub → Settings → Secrets → OCI_KUBECONFIG → Update"
  echo "3. Collez le contenu et sauvegardez"
  echo "4. Faites un git push pour tester"
else
  echo ""
  echo "❌ Échec de connexion au cluster"
  echo "Vérifiez que vous êtes bien connecté à OCI et que le cluster existe"
  exit 1
fi

