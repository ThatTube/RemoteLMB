package net.mcreator.morebosses.procedures;

import net.minecraft.world.entity.Entity;

public class CargaDeVentoProcedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		{
			// ===== entradas do bloco =====
			net.minecraft.world.entity.Entity _ent = entity;
			if (!(_ent instanceof net.minecraft.world.entity.LivingEntity _le))
				return;
			double _w = Math.max(1d, 10);
			double _h = Math.max(1d, 5);
			double _force = Math.max(0d, 5);
			boolean _sound = true;
			net.minecraft.world.level.Level _lvl = _le.level();
			// ===== base vectors (frente/direita) a partir do yaw do player =====
			float yaw = _le.getYRot();
			double fx = -Math.sin(Math.toRadians(yaw)); // frente X
			double fz = Math.cos(Math.toRadians(yaw)); // frente Z
			double rx = fz; // direita X
			double rz = -fx; // direita Z
			// centro da "parede" 1 bloco à frente e na meia-altura do player
			double cx = _le.getX() + fx * 1.0;
			double cy = _le.getY() + _le.getBbHeight() * 0.5;
			double cz = _le.getZ() + fz * 1.0;
			double halfW = _w / 2.0;
			double halfH = _h / 2.0;
			double depth = 2.0; // “parede” fininha com ~2 blocos de espessura
			// AABB alinhada ao mundo que cobre a parede à frente do player
			net.minecraft.world.phys.AABB box = new net.minecraft.world.phys.AABB(Math.min(cx - halfW * Math.abs(rx) - depth * Math.abs(fx), cx + halfW * Math.abs(rx) + depth * Math.abs(fx)), cy - halfH,
					Math.min(cz - halfW * Math.abs(rz) - depth * Math.abs(fz), cz + halfW * Math.abs(rz) + depth * Math.abs(fz)), Math.max(cx - halfW * Math.abs(rx) - depth * Math.abs(fx), cx + halfW * Math.abs(rx) + depth * Math.abs(fx)),
					cy + halfH, Math.max(cz - halfW * Math.abs(rz) - depth * Math.abs(fz), cz + halfW * Math.abs(rz) + depth * Math.abs(fz)));
			java.util.List<net.minecraft.world.entity.Entity> _targets = _lvl.getEntities(_le, box, e -> e.isAlive() && e != _le);
			// ===== mapeamento do “force” para empurrão e curvatura =====
			// Empurra ~proporcional a 'force' (≈ distância em blocos)
			double pushH = 0.25 * _force; // componente horizontal
			double liftY = 0.06 * _force + 0.05; // curvatura (mais força => mais arco)
			for (net.minecraft.world.entity.Entity e2 : _targets) {
				// só entidades realmente “à frente” (produto escalar > 0)
				double vx = e2.getX() - _le.getX();
				double vz = e2.getZ() - _le.getZ();
				double dotF = vx * fx + vz * fz;
				if (dotF <= 0)
					continue;
				// adiciona vetor de impulso
				net.minecraft.world.phys.Vec3 dv = new net.minecraft.world.phys.Vec3(fx * pushH, liftY, fz * pushH);
				e2.setDeltaMovement(e2.getDeltaMovement().add(dv));
				e2.hurtMarked = true;
			}
			// ===== som opcional =====
			if (_sound && _lvl != null && !_lvl.isClientSide) {
				net.minecraft.sounds.SoundEvent se = net.minecraft.sounds.SoundEvents.PLAYER_ATTACK_SWEEP;
				// FIX: usa o overload com coordenadas double (nada de BlockPos aqui)
				_lvl.playSound(null, _le.getX(), _le.getY(), _le.getZ(), se, net.minecraft.sounds.SoundSource.PLAYERS, 0.6F, 1.2F);
			}
		}
	}
}
