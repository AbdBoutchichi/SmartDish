#!/bin/bash
# Script pour générer un kubeconfig avec token pour OKE
# À exécuter dans OCI Cloud Shell

set -e

CLUSTER_ID="ocid1.cluster.oc1.eu-paris-1.aaaaaaaakhi5xnahycf4ozq2vinwsf3t6hbiwmomgq5quiqsvcq3gvzgw3tq"
REGION="eu-paris-1"

echo "=========================================="
echo "Génération du kubeconfig avec token"
echo "=========================================="
echo ""

# Récupérer les informations du cluster
echo "1. Récupération des informations du cluster..."
CLUSTER_INFO=$(oci ce cluster get --cluster-id "$CLUSTER_ID" --region "$REGION")

CLUSTER_NAME=$(echo "$CLUSTER_INFO" | jq -r '.data.name')
CLUSTER_ENDPOINT=$(echo "$CLUSTER_INFO" | jq -r '.data.endpoints["public-endpoint"]')
CLUSTER_CA=$(echo "$CLUSTER_INFO" | jq -r '.data["kubernetes-network-config"]["pods-cidr"]')

echo "Nom du cluster: $CLUSTER_NAME"
echo "Endpoint: $CLUSTER_ENDPOINT"
echo ""

# Générer un token
echo "2. Génération d'un token d'authentification..."
TOKEN=$(oci ce cluster generate-token --cluster-id "$CLUSTER_ID" --region "$REGION" | jq -r '.token')

if [ -z "$TOKEN" ]; then
    echo "❌ Erreur: impossible de générer un token"
    exit 1
fi

echo "✅ Token généré"
echo ""

# Créer le kubeconfig
echo "3. Création du kubeconfig..."
mkdir -p ~/.kube

cat > ~/.kube/config << EOF
apiVersion: v1
clusters:
- cluster:
    server: $CLUSTER_ENDPOINT
  name: cluster-$CLUSTER_ID
contexts:
- context:
    cluster: cluster-$CLUSTER_ID
    user: user-$CLUSTER_ID
  name: context-$CLUSTER_ID
current-context: context-$CLUSTER_ID
kind: Config
preferences: {}
users:
- name: user-$CLUSTER_ID
  user:
    token: $TOKEN
EOF

chmod 600 ~/.kube/config

echo "✅ Kubeconfig créé dans ~/.kube/config"
echo ""

# Vérifier
echo "4. Vérification..."
echo ""
cat ~/.kube/config | grep -A 3 "user:"
echo ""

# Tester la connexion
echo "5. Test de connexion au cluster..."
if kubectl get nodes; then
    echo ""
    echo "✅ Connexion au cluster réussie!"
    echo ""
    echo "6. Encodage du kubeconfig en base64..."
    cat ~/.kube/config | base64 -w 0 > ~/kubeconfig-base64.txt
    echo ""
    echo "✅ Fichier encodé créé: ~/kubeconfig-base64.txt"
    echo ""
    echo "Exécutez: cat ~/kubeconfig-base64.txt"
    echo "Copiez la sortie et mettez à jour le secret OCI_KUBECONFIG dans GitHub"
else
    echo ""
    echo "❌ Erreur de connexion au cluster"
    exit 1
fi

