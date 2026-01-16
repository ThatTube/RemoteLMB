package net.mcreator.morebosses.procedures;

import org.checkerframework.checker.units.qual.s;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.Entity;

import net.mcreator.morebosses.entity.CopperMonstrosityEntity;

public class CabutoProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		if (!(entity instanceof CopperMonstrosityEntity)) {
			if (world instanceof Level _level && !_level.isClientSide())
				_level.explode(null, x, y, z, 3, Level.ExplosionInteraction.NONE);
			{ // spawn_particles_custom.java.ftl (WORLD action) — nomes corrigidos, sem conflito com (x,y,z)
				// ---- Parâmetros do bloco ----
				final double _cx = x;
				final double _cy = y;
				final double _cz = z;
				final int _ticks = Math.max(0, (int) (0));
				final String _shape = "sphere";
				final double _radius = 3;
				final int _count = Math.max(1, (int) (600));
				final boolean _rotate = false;
				final double _speed = Math.max(0.01, Math.abs(1));
				final String _pid = "minecraft:sga_n";
				// ---- Mundo/nível ----
				if (!(world instanceof net.minecraft.server.level.ServerLevel _level))
					return;
				// Resolve partícula (fallback END_ROD)
				final net.minecraft.core.particles.ParticleOptions _ptype;
				{
					net.minecraft.core.particles.ParticleOptions tmp = net.minecraft.core.particles.ParticleTypes.END_ROD;
					try {
						var pt = net.minecraft.core.registries.BuiltInRegistries.PARTICLE_TYPE.get(new net.minecraft.resources.ResourceLocation(_pid));
						if (pt instanceof net.minecraft.core.particles.SimpleParticleType s)
							tmp = s;
					} catch (Exception ignored) {
					}
					_ptype = tmp;
				}
				final double TWO_PI = Math.PI * 2.0;
				final double DEG = 0.017453292519943295;
				java.util.function.DoubleUnaryOperator _frac = (v) -> v - Math.floor(v); // 'v' para não colidir com x/y/z
				// Centro fixo desta instância
				final net.minecraft.world.phys.Vec3 _centerMid = new net.minecraft.world.phys.Vec3(_cx, _cy, _cz);
				final net.minecraft.world.phys.Vec3 _centerFoot = new net.minecraft.world.phys.Vec3(_cx, _cy, _cz);
				// ---- Desenha 1 frame ----
				java.util.function.Consumer<net.minecraft.server.level.ServerLevel> _frame = (srv) -> {
					long gt = srv.getGameTime();
					double spin = _rotate ? ((gt * _speed) % 360.0) * DEG : 0.0;
					switch (_shape) {
						case "sphere" : {
							// Esfera Fibonacci em torno de (_cx,_cy,_cz)
							double ga = Math.PI * (3.0 - Math.sqrt(5.0));
							for (int i = 0; i < _count; i++) {
								double f = (i + 0.5) / (double) _count;
								double yy = 1.0 - 2.0 * f; // <- 'yy' para não colidir com parâmetro 'y'
								double r = Math.sqrt(Math.max(0.0, 1.0 - yy * yy));
								double th = ga * i + spin;
								double px = Math.cos(th) * r;
								double pz = Math.sin(th) * r;
								srv.sendParticles(_ptype, _centerMid.x + px * _radius, _centerMid.y + yy * _radius, _centerMid.z + pz * _radius, 1, 0, 0, 0, 0);
							}
							break;
						}
						case "ring" : {
							for (int i = 0; i < _count; i++) {
								double th = TWO_PI * i / (double) _count + spin;
								srv.sendParticles(_ptype, _centerFoot.x + Math.cos(th) * _radius, _centerFoot.y + 0.05, _centerFoot.z + Math.sin(th) * _radius, 1, 0, 0, 0, 0);
							}
							break;
						}
						case "expand" : {
							double base = 40.0;
							double cycle = Math.max(1.0, base / _speed);
							double prog = _frac.applyAsDouble(srv.getGameTime() / cycle);
							double rr = Math.max(0.1, _radius * prog);
							for (int i = 0; i < _count; i++) {
								double th = TWO_PI * i / (double) _count + spin;
								srv.sendParticles(_ptype, _centerFoot.x + Math.cos(th) * rr, _centerFoot.y + 0.05, _centerFoot.z + Math.sin(th) * rr, 1, 0, 0, 0, 0);
							}
							break;
						}
						case "cyl" : {
							// anel que sobe e desce gerando cilindro com o tempo
							double height = Math.max(1.0, _radius * 2.0);
							double base = 32.0;
							double cycle = Math.max(1.0, base / _speed);
							double prog = _frac.applyAsDouble(srv.getGameTime() / cycle);
							double tri = prog < 0.5 ? (prog * 2.0) : (2.0 - prog * 2.0);
							double yoff = tri * height;
							for (int i = 0; i < _count; i++) {
								double th = TWO_PI * i / (double) _count + spin;
								srv.sendParticles(_ptype, _centerFoot.x + Math.cos(th) * _radius, _centerFoot.y + yoff, _centerFoot.z + Math.sin(th) * _radius, 1, 0, 0, 0, 0);
							}
							break;
						}
						default : {
							// fallback = ring
							for (int i = 0; i < _count; i++) {
								double th = TWO_PI * i / (double) _count + spin;
								srv.sendParticles(_ptype, _centerFoot.x + Math.cos(th) * _radius, _centerFoot.y + 0.05, _centerFoot.z + Math.sin(th) * _radius, 1, 0, 0, 0, 0);
							}
						}
					}
				};
				// ---- Execução ----
				if (_ticks <= 0) {
					_frame.accept(_level); // 1 frame (se o chamador roda a cada tick, vira contínuo)
				} else {
					// Agenda 1 frame por tick por _ticks (funciona em qualquer trigger)
					new Object() {
						int left = _ticks;

						void start() {
							net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(this);
						}

						@net.minecraftforge.eventbus.api.SubscribeEvent
						public void onServerTick(net.minecraftforge.event.TickEvent.ServerTickEvent e) {
							if (e.phase != net.minecraftforge.event.TickEvent.Phase.END)
								return;
							_frame.accept(_level);
							if (--left <= 0)
								net.minecraftforge.common.MinecraftForge.EVENT_BUS.unregister(this);
						}
					}.start();
				}
			}
		}
	}
}
